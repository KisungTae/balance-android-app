package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.common.InvalidationListener
import com.beeswork.balance.data.database.common.PageSyncDateTracker
import com.beeswork.balance.data.database.common.QueryResult
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.database.entity.match.MatchCount
import com.beeswork.balance.data.database.entity.swipe.Click
import com.beeswork.balance.data.database.entity.swipe.SwipeCount
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.data.network.rds.report.ReportRDS
import com.beeswork.balance.data.network.response.match.ClickDTO
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.internal.constant.ClickResult
import com.beeswork.balance.internal.constant.MatchPageFilter
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.ui.mainviewpagerfragment.NewMatch
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.threeten.bp.OffsetDateTime
import java.util.*
import java.util.concurrent.Callable


class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val reportRDS: ReportRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val swipeDAO: SwipeDAO,
    private val clickDAO: ClickDAO,
    private val matchCountDAO: MatchCountDAO,
    private val swipeCountDAO: SwipeCountDAO,
    private val photoDAO: PhotoDAO,
    private val matchMapper: MatchMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider,
    private val ioDispatcher: CoroutineDispatcher
) : MatchRepository {

    private var newMatchInvalidationListener: InvalidationListener<NewMatch>? = null
    private val matchPageSyncDateTracker = PageSyncDateTracker()

    @ExperimentalCoroutinesApi
    override val newMatchFlow: Flow<NewMatch> = callbackFlow {
        newMatchInvalidationListener = object : InvalidationListener<NewMatch> {
            override fun onInvalidate(data: NewMatch) {
                offer(data)
            }
        }
        awaitClose { }
    }

    override suspend fun loadMatches(loadSize: Int, startPosition: Int, matchPageFilter: MatchPageFilter?): List<Match> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
            if (matchPageSyncDateTracker.shouldSyncPage(getMatchPageSyncDateTrackerKey(startPosition, matchPageFilter))) {
                syncMatches(loadSize, startPosition, matchPageFilter)
            }

            when (matchPageFilter) {
                null -> {
                    return@withContext matchDAO.getAllPagedBy(accountId, loadSize, startPosition)
                }
                MatchPageFilter.MATCH -> {
                    return@withContext matchDAO.getMatchesPagedBy(accountId, loadSize, startPosition)
                }
                MatchPageFilter.CHAT -> {
                    return@withContext matchDAO.getChatsPagedBy(accountId, loadSize, startPosition)
                }
                MatchPageFilter.CHAT_WITH_MESSAGES -> {
                    matchDAO.getChatsWithMessagesPagedBy(accountId, loadSize, startPosition)
                }
            }
        }
    }

    private fun syncMatches(loadSize: Int, startPosition: Int, matchPageFilter: MatchPageFilter?) {
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { _, _ -> }) {
            val matchPageSyncDateTrackerKey = getMatchPageSyncDateTrackerKey(startPosition, matchPageFilter)
            matchPageSyncDateTracker.updateSyncDate(matchPageSyncDateTrackerKey, OffsetDateTime.now())
            val response = matchRDS.listMatches(loadSize, startPosition, matchPageFilter)
            if (response.isError()) {
                matchPageSyncDateTracker.updateSyncDate(matchPageSyncDateTrackerKey, null)
                return@launch
            }
            balanceDatabase.runInTransaction {
                response.data?.let { listMatchesDTO ->
                    saveMatches(listMatchesDTO)
                }
            }
        }
    }

    override suspend fun fetchMatches(loadSize: Int, lastSwipedId: UUID?, matchPageFilter: MatchPageFilter?): Resource<ListMatchesDTO> {
        return withContext(ioDispatcher) {
            val response = matchRDS.fetchMatches(loadSize, lastSwipedId, matchPageFilter)
            if (response.isError()) {
                return@withContext response
            }
            balanceDatabase.runInTransaction {
                response.data?.let { listMatchesDTO ->
                    saveMatches(listMatchesDTO)
                }
            }
            return@withContext response
        }
    }

    private fun saveMatches(listMatchesDTO: ListMatchesDTO) {
        val swipeCount = swipeCountDAO.getBy(preferenceProvider.getAccountId())
        listMatchesDTO.matchDTOs.forEach { matchDTO ->
            val match = insertMatch(matchDTO).data
            if (match != null && swipeCount != null) {
                deleteSwipe(match, swipeCount)
            }
        }
        updateMatchCount(listMatchesDTO.matchCount, listMatchesDTO.matchCountCountedAt)
        if (swipeCount != null) {
            swipeCountDAO.insert(swipeCount)
        }
    }

    private fun insertMatch(matchDTO: MatchDTO): QueryResult<Match> {
        clickDAO.insert(Click(matchDTO.swiperId, matchDTO.swipedId))
        val match = matchDAO.getBy(matchDTO.chatId)
        if (match == null || !match.isEqualTo(matchDTO)) {
            val newMatch = matchMapper.toMatch(matchDTO)
            matchDAO.insert(newMatch)
            return if (match == null) {
                QueryResult.insert(newMatch)
            } else {
                QueryResult.update(newMatch)
            }
        }
        return QueryResult.none()
    }

    private fun deleteSwipe(match: Match) {
        val swipeCount = swipeCountDAO.getBy(match.swiperId)
        if (swipeCount != null) {
            deleteSwipe(match, swipeCount)
            swipeCountDAO.insert(swipeCount)
        }
    }

    private fun deleteSwipe(match: Match, swipeCount: SwipeCount) {
        val deletedSwipeCount = swipeDAO.deleteBy(match.swipedId, match.swiperId)
        if (swipeCount.countedAt.isBefore(match.createdAt) && swipeCount.count > 0) {
            swipeCount.count = swipeCount.count - deletedSwipeCount
        }
    }

    private fun updateMatchCount(count: Long, countedAt: OffsetDateTime) {
        val accountId = preferenceProvider.getAccountId() ?: return
        val matchCount = matchCountDAO.getBy(accountId)
        if (matchCount == null) {
            matchCountDAO.insert(MatchCount(accountId, count, countedAt))
        } else {
            if (countedAt.isAfter(matchCount.countedAt)) {
                matchCount.count = count
                matchCount.countedAt = countedAt
                matchCountDAO.insert(matchCount)
            }
        }
    }

    private fun incrementMatchCount(match: Match) {
        val matchCount = matchCountDAO.getBy(match.swiperId)
        if (matchCount == null) {
            matchCountDAO.insert(MatchCount(match.swiperId, 1))
        } else {
            if (match.createdAt.isAfter(matchCount.countedAt)) {
                matchCount.count++
                matchCountDAO.insert(matchCount)
            }
        }
    }

    private fun doSaveMatch(matchDTO: MatchDTO?): QueryResult<Match> {
        if (matchDTO == null) {
            return QueryResult.none()
        }
        return balanceDatabase.runInTransaction(Callable {
            val queryResult = insertMatch(matchDTO)
            if (queryResult.isInsert() && queryResult.data != null) {
                deleteSwipe(queryResult.data)
                incrementMatchCount(queryResult.data)
            }
            return@Callable queryResult
        })
    }

    override suspend fun saveMatch(matchDTO: MatchDTO) {
        withContext(Dispatchers.IO) {
            val queryResult = doSaveMatch(matchDTO)
            val accountId = preferenceProvider.getAccountId()
            val match = queryResult.data
            if (queryResult.isInsert() && match != null && match.swiperId == accountId) {
                val newMatch = NewMatch(accountId, photoDAO.getProfilePhotoBy(accountId), match.swipedId, match.swipedProfilePhotoKey)
                newMatchInvalidationListener?.onInvalidate(newMatch)
            }
        }
    }

    override suspend fun click(swipedId: UUID, answers: Map<Int, Boolean>): Resource<ClickDTO> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
            val response = matchRDS.click(swipedId, answers)
            response.data?.let { clickDTO ->
                when (clickDTO.clickResult) {
                    ClickResult.MATCHED -> {
                        doSaveMatch(clickDTO.matchDTO)
                    }
                    ClickResult.CLICKED -> {
                        clickDAO.insert(Click(swipedId, accountId))
                    }
                    ClickResult.MISSED -> {
                    }
                }
            }
            return@withContext response
        }
    }

    private fun getMatchPageSyncDateTrackerKey(startPosition: Int, matchPageFilter: MatchPageFilter?): String {
        return matchPageFilter?.toString() ?: "" + startPosition
    }

    override suspend fun unmatch(chatId: UUID, swipedId: UUID): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val response = matchRDS.unmatch(swipedId)
            if (response.isSuccess()) {
                unmatch(chatId)
            }
            return@withContext response
        }
    }

    private fun unmatch(chatId: UUID) {
        balanceDatabase.runInTransaction {
            matchDAO.deleteBy(chatId)
            chatMessageDAO.deleteByChatId(chatId)
        }
    }

    override suspend fun reportMatch(
        chatId: UUID,
        swipedId: UUID,
        reportReason: ReportReason,
        description: String
    ): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val response = reportRDS.reportMatch(swipedId, reportReason, description)
            if (response.isSuccess()) {
                unmatch(chatId)
            }
            return@withContext response
        }
    }

    override fun getMatchPageInvalidationFlow(): Flow<Boolean> {
        return matchDAO.getPageInvalidationFlow()
    }

    override fun getMatchCountFlow(): Flow<Long?> {
        return matchCountDAO.getCountFlowBy(preferenceProvider.getAccountId())
    }

    override fun getMatchFlow(chatId: UUID): Flow<Match?> {
        return matchDAO.getMatchFlowBy(chatId)
    }

    override suspend fun deleteMatches() {
        withContext(ioDispatcher) { matchDAO.deleteAllBy(preferenceProvider.getAccountId()) }
    }

    override suspend fun deleteMatchCount() {
        withContext(ioDispatcher) {
            matchCountDAO.deleteBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun isUnmatched(chatId: UUID): Boolean {
        return withContext(ioDispatcher) {
            return@withContext matchDAO.isUnmatchedBy(chatId) ?: true
        }
    }

    override fun testFunction() {
        val newMatch = NewMatch(UUID.randomUUID(), "", UUID.randomUUID(), "")
        newMatchInvalidationListener?.onInvalidate(newMatch)
    }
}