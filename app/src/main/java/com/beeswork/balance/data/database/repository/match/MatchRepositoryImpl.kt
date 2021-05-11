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
import com.beeswork.balance.data.database.tuple.MatchProfileTuple
import com.beeswork.balance.data.network.rds.report.ReportRDS
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.data.network.service.stomp.StompClient
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import java.util.*
import kotlin.random.Random


class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val reportRDS: ReportRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val clickDAO: ClickDAO,
    private val swipeDAO: SwipeDAO,
    private val matchMapper: MatchMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider,
    private val stompClient: StompClient,
    private val scope: CoroutineScope
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

    init {
        collectMatchFlow()
    }

    private fun collectMatchFlow() {
        stompClient.matchFlow.onEach { matchDTO ->
            saveMatchAndOffer(matchMapper.fromDTOToEntity(matchDTO))
        }.launchIn(scope)
    }

    override suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.findAllPaged(loadSize, startPosition)
        }
    }

    override suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.findAllPaged(loadSize, startPosition, "%${searchKeyword}%")
        }
    }

    override suspend fun fetchMatches(): Resource<ListMatchesDTO> {
        return withContext(Dispatchers.IO) {
            val fetchedAt = OffsetDateTime.now()
            val response = matchRDS.listMatches(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                preferenceProvider.getMatchFetchedAt()
            )
            response.data?.let { data ->
                balanceDatabase.runInTransaction {
                    data.matchDTOs?.forEach { matchDTO ->
                        saveMatch(matchMapper.fromDTOToEntity(matchDTO))
                    }
                }
                preferenceProvider.putMatchFetchedAt(data.fetchedAt)
                data.matchDTOs = null
                data.fetchedAt = fetchedAt
            }
            return@withContext response
        }
    }

    private fun saveMatch(match: Match) {
        updateMatch(match)
        swipeDAO.insert(Swipe(match.swipedId))
        clickDAO.deleteBySwiperId(match.swipedId)
        matchDAO.insert(match)
    }

    private fun updateMatch(match: Match) {
        matchDAO.findById(match.chatId)?.let {
            if (!match.unmatched) {
                match.updatedAt = it.updatedAt
                match.recentChatMessage = it.recentChatMessage
                match.active = it.active
            }
            match.unread = it.unread
            match.lastReadChatMessageId = it.lastReadChatMessageId
        }
    }

    override suspend fun synchronizeMatch(chatId: Long) {
        withContext(Dispatchers.IO) {
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
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.findUnmatched(chatId)
        }
    }

    override suspend fun unmatch(chatId: Long, swipedId: UUID): Resource<EmptyResponse> {
        return withContext(Dispatchers.IO) {
            val response = matchRDS.unmatch(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                swipedId
            )
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
        return withContext(Dispatchers.IO) {
            val response = reportRDS.reportMatch(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                swipedId,
                reportReason,
                description
            )
            if (response.isSuccess()) unmatch(chatId)
            return@withContext response
        }
    }

    override suspend fun saveMatch(matchDTO: MatchDTO) {
        withContext(Dispatchers.IO) { saveMatchAndOffer(matchMapper.fromDTOToEntity(matchDTO)) }
    }

    private fun saveMatchAndOffer(match: Match) {
        saveMatch(match)
        newMatchFlowListener?.onReceive(matchMapper.fromEntityToProfileTuple(match))
    }

    override suspend fun getMatchInvalidation(): Flow<Boolean> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.invalidation()
        }
    }


    //  TODO: remove me
    private fun createDummyChatMessages() {
        val messages = mutableListOf<ChatMessage>()
        var count = 1L
        var now = OffsetDateTime.now()
        val chatId = matchDAO.findAllPaged(100, 0)[0].chatId

        for (i in 1..10) {
            val status = if (Random.nextBoolean()) ChatMessageStatus.SENDING else ChatMessageStatus.ERROR
            messages.add(ChatMessage(chatId, "$count - ${Random.nextLong()}", status, null))
            count++
        }

        for (i in 0..100) {
            var createdAt = now.plusMinutes(Random.nextInt(10).toLong())
            for (j in 0..Random.nextInt(10)) {
                if ((Random.nextInt(3) + 1) % 3 == 0) createdAt = createdAt.plusMinutes(Random.nextInt(10).toLong())
                val status = if (Random.nextBoolean()) ChatMessageStatus.SENT else ChatMessageStatus.RECEIVED
                messages.add(ChatMessage(chatId, "$count - ${Random.nextLong()}", status, createdAt, count))
                count++
            }
            now = now.plusDays(1)
        }
        chatMessageDAO.insert(messages)
    }


    //  TODO: remove me
    private fun saveSentChatMessages(sentChatMessages: List<ChatMessage>, matches: List<Match>) {
        val chatIds = matches.map { it.chatId }
        for (msg in sentChatMessages) {
            val randomIndex = Random.nextInt(0, chatIds.size - 1)
            chatMessageDAO.insert(
                ChatMessage(
                    chatIds[randomIndex],
                    "message-${Random.nextFloat()}",
                    ChatMessageStatus.SENDING,
                    OffsetDateTime.now(ZoneOffset.UTC),
                    msg.key,
                    msg.id,
                )
            )
        }
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
        CoroutineScope(Dispatchers.IO).launch {
//            createDummyMatch()
//            createDummyChatMessages()
//            matchDAO.insert(Match(Random.nextLong(), UUID.randomUUID(), false, false, "test match", "", OffsetDateTime.now()))
            matchDAO.updateAsUnmatched(3)
        }
    }
}


// 698F2EB63FEF4EE39C7D3E527740548E

// 698f2eb6-3fef-4ee3-9c7d-3e527740548e


//insert into click values ('44D7C228F6704FE78302CDFD7FDAA912', '',	'2021-05-05T03:04:58.941Z')
//insert into click values ('698F2EB63FEF4EE39C7D3E527740548E', '',	'2021-05-05T14:02:55.728Z')
//insert into click values ('825850302F0E4BE5BBF1BBCCE26D0408', '',	'2021-05-05T03:04:58.941Z')
//insert into click values ('CD4F05BF11924F1690C7F97B46584BA6', '',	'2021-05-05T03:04:58.941Z')

//1	825850302F0E4BE5BBF1BBCCE26D0408
//2	698F2EB63FEF4EE39C7D3E527740548E
//3	CD4F05BF11924F1690C7F97B46584BA6
//4	44D7C228F6704FE78302CDFD7FDAA912