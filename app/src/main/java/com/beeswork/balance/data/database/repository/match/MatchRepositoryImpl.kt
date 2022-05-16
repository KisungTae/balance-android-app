package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.common.CallBackFlowListener
import com.beeswork.balance.data.database.common.PageFetchDateTracker
import com.beeswork.balance.data.database.common.QueryResult
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.database.entity.match.MatchCount
import com.beeswork.balance.data.database.entity.swipe.SwipeCount
import com.beeswork.balance.data.database.repository.BaseRepository
import com.beeswork.balance.data.database.result.ClickResult
import com.beeswork.balance.data.network.rds.login.LoginRDS
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.data.network.response.match.*
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.internal.constant.ClickOutcome
import com.beeswork.balance.internal.constant.MatchPageFilter
import com.beeswork.balance.internal.constant.ReportReason
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.OffsetDateTime
import java.util.*
import java.util.concurrent.Callable


class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    loginRDS: LoginRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val swipeDAO: SwipeDAO,
    private val matchCountDAO: MatchCountDAO,
    private val swipeCountDAO: SwipeCountDAO,
    private val photoDAO: PhotoDAO,
    private val matchMapper: MatchMapper,
    private val stompClient: StompClient,
    private val applicationScope: CoroutineScope,
    private val balanceDatabase: BalanceDatabase,
    preferenceProvider: PreferenceProvider,
    private val ioDispatcher: CoroutineDispatcher
) : BaseRepository(loginRDS, preferenceProvider), MatchRepository {

    private val matchPageFetchDateTracker = PageFetchDateTracker(5L)

    private var newMatchCallBackFlowListener: CallBackFlowListener<Match>? = null

    @ExperimentalCoroutinesApi
    override val newMatchFlow: Flow<Match> = callbackFlow {
        newMatchCallBackFlowListener = object : CallBackFlowListener<Match> {
            override fun onInvoke(data: Match) {
                offer(data)
            }
        }
        awaitClose { }
    }

    init {
        stompClient.matchFlow.onEach { matchDTO ->
            saveMatch(matchDTO)
        }.launchIn(applicationScope)
    }

    override suspend fun loadMatches(loadSize: Int, startPosition: Int, matchPageFilter: MatchPageFilter?): List<Match> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
            if (matchPageFetchDateTracker.shouldFetchPage(getMatchPageFetchDateTrackerKey(startPosition, matchPageFilter))) {
                listMatches(loadSize, startPosition, matchPageFilter)
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

    private fun listMatches(loadSize: Int, startPosition: Int, matchPageFilter: MatchPageFilter?) {
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { _, _ -> }) {
            val matchPageSyncDateTrackerKey = getMatchPageFetchDateTrackerKey(startPosition, matchPageFilter)
            matchPageFetchDateTracker.updateFetchDate(matchPageSyncDateTrackerKey, OffsetDateTime.now())
            val response = matchRDS.listMatches(loadSize, startPosition, matchPageFilter)
            if (response.isError()) {
                matchPageFetchDateTracker.updateFetchDate(matchPageSyncDateTrackerKey, null)
                return@launch
            }
            balanceDatabase.runInTransaction {
                response.data?.let { listMatchesDTO ->
                    saveMatches(listMatchesDTO)
                }
            }
        }
    }

    override suspend fun fetchMatches(loadSize: Int, lastMatchId: Long?, matchPageFilter: MatchPageFilter?): Resource<ListMatchesDTO> {
        return withContext(ioDispatcher) {
            val response = matchRDS.fetchMatches(loadSize, lastMatchId, matchPageFilter)
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
                match.swiperProfilePhotoKey = photoDAO.getProfilePhotoKeyBy(match.swiperId)
                newMatchCallBackFlowListener?.onInvoke(match)
            }
        }
    }

    override suspend fun click(swipedId: UUID, answers: Map<Int, Boolean>): Resource<ClickResult> {
        return withContext(ioDispatcher) {
            val response = matchRDS.click(swipedId, answers)
            return@withContext response.map { clickResponse ->
                if (clickResponse == null) {
                    return@map null
                }

                when (clickResponse.clickOutcome) {
                    ClickOutcome.MATCHED -> {
                        val saveMatchResult = doSaveMatch(clickResponse.matchDTO)
                        val match = saveMatchResult.data
                        match?.swiperProfilePhotoKey = photoDAO.getProfilePhotoKeyBy(match?.swiperId)
                        return@map ClickResult(clickResponse.clickOutcome, clickResponse.point, match)
                    }
                    ClickOutcome.CLICKED -> {
                        return@map ClickResult(clickResponse.clickOutcome, clickResponse.point, null)
                    }
                    ClickOutcome.MISSED -> {
                        return@map ClickResult(clickResponse.clickOutcome, clickResponse.point, null)
                    }
                }
            }
        }
    }

    private fun getMatchPageFetchDateTrackerKey(startPosition: Int, matchPageFilter: MatchPageFilter?): String {
        return matchPageFilter?.toString() ?: "" + startPosition
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

    override fun syncMatch(chatId: UUID) {
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { _, _ -> }) {
            val lastReceivedChatMessageId = chatMessageDAO.getLastReceivedChatMessageId(chatId) ?: return@launch
            val lastReadReceivedChatMessageId = matchDAO.getLastReadReceivedChatMessageIdBy(chatId) ?: 0

            if (lastReadReceivedChatMessageId >= lastReceivedChatMessageId) {
                return@launch
            }
            val response = getResponse { matchRDS.syncMatch(chatId, lastReceivedChatMessageId) }
            if (response.isSuccess()) {
                balanceDatabase.runInTransaction {
                    val currentLastReadReceivedChatMessageId = matchDAO.getLastReadReceivedChatMessageIdBy(chatId) ?: 0
                    if (currentLastReadReceivedChatMessageId < lastReceivedChatMessageId) {
                        matchDAO.updateLastReadReceivedChatMessageIdBy(chatId, lastReceivedChatMessageId)
                    }
                }
            }
        }
    }

    override suspend fun unmatch(chatId: UUID, swipedId: UUID): Resource<UnmatchDTO> {
        return withContext(ioDispatcher) {
            val response = matchRDS.unmatch(swipedId)
            if (response.isSuccess()) {
                unmatch(chatId)
            }
            if (response.data != null) {
                updateMatchCount(response.data.matchCount, response.data.matchCountCountedAt)
            }
            return@withContext response
        }
    }

    override suspend fun reportMatch(
        swipedId: UUID,
        reportReason: ReportReason,
        reportDescription: String?
    ): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val response = matchRDS.reportMatch(swipedId, reportReason, reportDescription)
            if (response.isSuccess()) {
                unmatch(matchDAO.getChatIdBy(swipedId))
            }
            if (response.data != null) {
                updateMatchCount(response.data.matchCount, response.data.matchCountCountedAt)
            }
            return@withContext response.toEmptyResponse()
        }
    }

    private fun unmatch(chatId: UUID?) {
        balanceDatabase.runInTransaction {
            matchDAO.deleteBy(chatId)
            chatMessageDAO.deleteBy(chatId)
        }
    }

    override fun testFunction() {
//        val newMatch = NewMatch(UUID.randomUUID(), "", UUID.randomUUID(), "")
//        newMatchCallBackFlowListener?.onInvoke(newMatch)
    }
}