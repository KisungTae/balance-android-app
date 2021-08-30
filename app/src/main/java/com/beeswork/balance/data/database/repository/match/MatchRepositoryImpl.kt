package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.data.network.rds.report.ReportRDS
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.internal.constant.PushType
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.OffsetDateTime
import java.util.*
import kotlin.random.Random


class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val reportRDS: ReportRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val clickDAO: ClickDAO,
    private val swipeDAO: SwipeDAO,
    private val fetchInfoDAO: FetchInfoDAO,
    private val matchMapper: MatchMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider,
    private val ioDispatcher: CoroutineDispatcher
) : MatchRepository {

    private var newMatchFlowListener: NewMatchFlowListener? = null

    @ExperimentalCoroutinesApi
    override val newMatchFlow = callbackFlow<MatchProfileTuple> {
        newMatchFlowListener = object : NewMatchFlowListener {
            override fun onReceive(matchProfileTuple: MatchProfileTuple) {
                offer(matchProfileTuple)
            }
        }
        awaitClose { }
    }

    override suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match> {
        return withContext(ioDispatcher) {
            return@withContext matchDAO.findAllPaged(preferenceProvider.getAccountId(), loadSize, startPosition)
        }
    }

    override suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match> {
        return withContext(ioDispatcher) {
            return@withContext matchDAO.findAllPaged(
                preferenceProvider.getAccountId(),
                loadSize,
                startPosition,
                "%${searchKeyword}%"
            )
        }
    }

    override suspend fun fetchMatches(): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
            val response = matchRDS.listMatches(accountId, fetchInfoDAO.findMatchFetchedAt(accountId))

            response.data?.let { data ->
                balanceDatabase.runInTransaction {
                    data.matchDTOs?.forEach { matchDTO ->
                        val match = matchMapper.toMatch(matchDTO)
                        match.swiperId = accountId
                        saveMatch(match)
                    }
                }
                fetchInfoDAO.updateMatchFetchedAt(accountId, data.fetchedAt)
            }
            return@withContext response.toEmptyResponse()
        }
    }

    private fun saveMatch(match: Match) {
        updateMatch(match)
        val accountId = preferenceProvider.getAccountId()
        swipeDAO.insert(Swipe(match.swipedId, accountId))
        clickDAO.deleteBySwiperId(accountId, match.swipedId)
        matchDAO.insert(match)
    }

    private fun updateMatch(match: Match) {
        matchDAO.findById(match.chatId)?.let {
            if (!match.unmatched) {
                match.updatedAt = it.updatedAt
                match.recentChatMessage = it.recentChatMessage
                match.active = it.active
                chatMessageDAO.findMostRecentAfter(match.chatId, match.lastReadChatMessageId)?.let { chatMessage ->
                    match.recentChatMessage = chatMessage.body
                    match.updatedAt = chatMessage.createdAt
                    match.active = true
                }
                match.unread = chatMessageDAO.existAfter(match.chatId, match.lastReadChatMessageId)
            }
            match.lastReadChatMessageId = it.lastReadChatMessageId
        }

    }

    override suspend fun synchronizeMatch(chatId: Long) {
        withContext(ioDispatcher) {
            balanceDatabase.runInTransaction {
                matchDAO.findById(chatId)?.let { match ->
                    chatMessageDAO.findMostRecentAfter(chatId, match.lastReadChatMessageId)?.let { chatMessage ->
                        match.lastReadChatMessageId = chatMessage.id
                        match.recentChatMessage = chatMessage.body
                        match.unread = false
                        matchDAO.insert(match)
                    }
                }
            }
        }
    }

    override suspend fun isUnmatched(chatId: Long): Boolean {
        return withContext(ioDispatcher) {
            return@withContext matchDAO.findUnmatched(chatId)
        }
    }

    override suspend fun unmatch(chatId: Long, swipedId: UUID): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val response = matchRDS.unmatch(preferenceProvider.getAccountId(), swipedId)
            if (response.isSuccess()) unmatch(chatId)
            return@withContext response
        }
    }

    private fun unmatch(chatId: Long) {
        balanceDatabase.runInTransaction {
            matchDAO.delete(chatId)
            chatMessageDAO.deleteByChatId(chatId)
        }
    }

    override suspend fun reportMatch(
        chatId: Long,
        swipedId: UUID,
        reportReason: ReportReason,
        description: String
    ): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val response = reportRDS.reportMatch(
                preferenceProvider.getAccountId(),
                swipedId,
                reportReason,
                description
            )
            if (response.isSuccess()) unmatch(chatId)
            return@withContext response
        }
    }

    override suspend fun saveMatch(matchDTO: MatchDTO) {
        withContext(Dispatchers.IO) {
            val match = matchMapper.toMatch(matchDTO)
            saveMatch(match)
            if (match.swiperId == preferenceProvider.getAccountId())
                newMatchFlowListener?.onReceive(matchMapper.toProfileTuple(match))
        }
    }

    override fun getMatchInvalidationFlow(): Flow<Boolean> {
        return matchDAO.invalidation()
    }

    override fun getUnreadMatchCountFlow(): Flow<Int> {
        return matchDAO.countUnread(preferenceProvider.getAccountId())
    }

    override suspend fun click(swipedId: UUID, answers: Map<Int, Boolean>): Resource<PushType> {
        return withContext(ioDispatcher) {
            val response = matchRDS.click(preferenceProvider.getAccountId(), swipedId, answers)
            response.data?.let { matchDTO ->
                when (matchDTO.pushType) {
                    PushType.MATCHED -> saveMatch(matchMapper.toMatch(matchDTO))
                    PushType.CLICKED -> swipeDAO.insert(Swipe(swipedId, preferenceProvider.getAccountId()))
                    else -> println()
                }
                return@withContext response.mapData(matchDTO.pushType)
            }
            return@withContext response.mapData(null)
        }
    }

    override suspend fun deleteMatches() {
        withContext(ioDispatcher) { matchDAO.deleteAll(preferenceProvider.getAccountId()) }
    }


    //  TODO: remove me
    private fun createDummyChatMessages() {
        val messages = mutableListOf<ChatMessage>()
        var count = 1L
        var now = OffsetDateTime.now()
//        val chatId = matchDAO.findAllPaged(100, 0)[0].chatId

        for (i in 1..10) {
            val status = if (Random.nextBoolean()) ChatMessageStatus.SENDING else ChatMessageStatus.ERROR
//            messages.add(ChatMessage(chatId, "$count - ${Random.nextLong()}", status, null))
            count++
        }
//
//        for (i in 0..100) {
//            var createdAt = now.plusMinutes(Random.nextInt(10).toLong())
//            for (j in 0..Random.nextInt(10)) {
//                if ((Random.nextInt(3) + 1) % 3 == 0) createdAt = createdAt.plusMinutes(Random.nextInt(10).toLong())
//                val status = if (Random.nextBoolean()) ChatMessageStatus.SENT else ChatMessageStatus.RECEIVED
//                messages.add(ChatMessage(chatId, "$count - ${Random.nextLong()}", status, createdAt, count))
//                count++
//            }
//            now = now.plusDays(1)
//        }
//        chatMessageDAO.insert(messages)
    }


    //  TODO: remove me
//    private fun createDummyMatch() {
//        for ((count, i) in (1..10).withIndex()) {
//            matchDAO.insert(
//                Match(
//                    chatId = count.toLong(),
//                    matchedId = UUID.randomUUID(),
//                    active = false,
//                    unmatched = false,
//                    name = "user-$count",
//                    profilePhotoKey = "",
//                    deleted = false,
//                    updatedAt = OffsetDateTime.now()
//                )
//            )
//        }
//    }


    //  TODO: remove me
    override fun testFunction() {
//        _chatMessageReceiptLiveData.postValue(Resource.error(""))
        CoroutineScope(ioDispatcher).launch {
//            createDummyMatch()
//            createDummyChatMessages()
//            matchDAO.insert(Match(Random.nextLong(), UUID.randomUUID(), false, false, "test match", "", OffsetDateTime.now()))
//            matchDAO.updateAsUnmatched(3)
//            chatMessageDAO.updateStatusByKey(null, ChatMessageStatus.ERROR)
//            for (i in 0..10) {
//                chatMessageDAO.insert(ChatMessage(1, "test", ChatMessageStatus.SENDING, OffsetDateTime.now()))
//            }

//            val cm = chatMessageDAO.findByKey(3)
//            val cm2 = chatMessageDAO.findByKey(null)
//            println("$cm")
//            println("$cm2")

//            val list = listOf<Long>()


        }
    }
}