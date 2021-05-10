package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.database.repository.chat.ChatMessagePagingRefresh
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.data.database.repository.chat.ChatMessagePagingRefreshListener
import com.beeswork.balance.data.database.tuple.MatchProfileTuple
import com.beeswork.balance.data.network.rds.report.ReportRDS
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.internal.util.safeLet
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
    private val chatRDS: ChatRDS,
    private val reportRDS: ReportRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val clickDAO: ClickDAO,
    private val swipeDAO: SwipeDAO,
    private val matchMapper: MatchMapper,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider,
    private val stompClient: StompClient,
    private val scope: CoroutineScope
) : MatchRepository {

    private var chatMessagePagingRefreshListener: ChatMessagePagingRefreshListener? = null
    private var newMatchFlowListener: NewMatchFlowListener? = null

    @ExperimentalCoroutinesApi
    override val chatMessagePagingRefreshFlow = callbackFlow<ChatMessagePagingRefresh> {
        chatMessagePagingRefreshListener = object : ChatMessagePagingRefreshListener {
            override fun onRefresh(chatMessagePagingRefresh: ChatMessagePagingRefresh) {
                offer(chatMessagePagingRefresh)
            }
        }
        awaitClose {}
    }

    @ExperimentalCoroutinesApi
    override val newMatchFlow = callbackFlow<MatchProfileTuple> {
        newMatchFlowListener = object : NewMatchFlowListener {
            override fun onReceive(matchProfileTuple: MatchProfileTuple) {
                offer(matchProfileTuple)
            }
        }
        awaitClose {  }
    }

    init {
        collectNewMatchFlow()
    }

    private fun collectNewMatchFlow() {
        stompClient.newMatchFlow.onEach { matchDTO ->
            val match = matchMapper.fromDTOToEntity(matchDTO)
            saveMatch(match)
            newMatchFlowListener?.onReceive(matchMapper.fromEntityToProfileTuple(match))
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

    override suspend fun fetchMatches(): Resource<EmptyResponse> {
        return withContext(Dispatchers.IO) {
            val fetchedAt = OffsetDateTime.now()
            val listMatches = matchRDS.listMatches(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                preferenceProvider.getMatchFetchedAt()
            )
            listMatches.data?.let { data ->
                saveMatches(data.matchDTOs)
                saveChatMessages(data.sentChatMessageDTOs, data.receivedChatMessageDTOs)
                updateSendingChatMessages(fetchedAt)
                preferenceProvider.putMatchFetchedAt(data.fetchedAt)
                val chatMessagePagingRefresh = ChatMessagePagingRefresh(ChatMessagePagingRefresh.Type.FETCHED)
                chatMessagePagingRefreshListener?.onRefresh(chatMessagePagingRefresh)
            }
            return@withContext listMatches.toEmptyResponse()
        }
    }

    private fun saveChatMessages(
        sentChatMessageDTOs: List<ChatMessageDTO>,
        receivedChatMessageDTOs: List<ChatMessageDTO>
    ) {
        val receivedChatMessages = receivedChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) }
        val chatIds = mutableSetOf<Long>()
        val sentChatMessageIds = mutableListOf<Long>()
        val receivedChatMessageIds = mutableListOf<Long>()

        balanceDatabase.runInTransaction {
            receivedChatMessages.forEach {
                if (!chatMessageDAO.existById(it.id)) chatMessageDAO.insert(it)
                chatIds.add(it.chatId)
                receivedChatMessageIds.add(it.id)
            }

            sentChatMessageDTOs.forEach { chatMessageDTO ->
                safeLet(chatMessageDTO.key, chatMessageDTO.id) { key, id ->
                    chatMessageDAO.findByKey(key)?.let { chatMessage ->
                        chatMessage.id = id
                        chatMessage.status = ChatMessageStatus.SENT
                        chatMessage.createdAt = chatMessageDTO.createdAt
                        chatMessageDAO.insert(chatMessage)
                        chatIds.add(chatMessage.chatId)
                    }
                    sentChatMessageIds.add(id)
                }
            }
        }
        syncChatMessages(sentChatMessageIds, receivedChatMessageIds)
        updateRecentChatMessages(chatIds)
    }

    private fun updateSendingChatMessages(fetchedAt: OffsetDateTime) {
        chatMessageDAO.updateStatusByStatusAndCreatedAt(ChatMessageStatus.ERROR, ChatMessageStatus.SENDING, fetchedAt)
    }

    private fun updateRecentChatMessages(chatIds: Set<Long>) {
        balanceDatabase.runInTransaction {
            chatIds.forEach { chatId ->
                matchDAO.findById(chatId)?.let { match ->
                    if (!match.unmatched) updateRecentChatMessage(match)
                    updateUnread(match)
                    matchDAO.insert(match)
                }
            }
        }
    }

    private fun updateRecentChatMessage(match: Match) {
        chatMessageDAO.findMostRecentAfter(
            match.chatId,
            match.lastReadChatMessageId
        )?.let { chatMessage ->
            updateRecentChatMessage(match, chatMessage)
        }
    }

    private fun updateRecentChatMessage(match: Match, chatMessage: ChatMessage) {
        match.recentChatMessage = chatMessage.body
        chatMessage.createdAt?.let { match.updatedAt = it }
        match.active = true
    }

    private fun updateUnread(match: Match) {
        match.unread = chatMessageDAO.existByIdGreaterThan(match.chatId, match.lastReadChatMessageId)
    }

    private fun syncChatMessages(
        sentChatMessageIds: List<Long>,
        receivedChatMessageIds: List<Long>
    ) {
        if (sentChatMessageIds.isEmpty() && receivedChatMessageIds.isEmpty()) return
        CoroutineScope(Dispatchers.IO).launch(CoroutineExceptionHandler { c, t -> }) {
            chatRDS.syncChatMessages(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                sentChatMessageIds,
                receivedChatMessageIds
            )
        }
    }

    private fun saveMatches(matchDTOs: List<MatchDTO>) {
        val matches = matchDTOs.map { matchMapper.fromDTOToEntity(it) }

        balanceDatabase.runInTransaction {
            matches.forEach {
                updateMatch(it)
                swipeDAO.insert(Swipe(it.swipedId))
                matchDAO.insert(it)
                clickDAO.deleteBySwiperId(it.swipedId)
            }
        }
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
                        if (!match.unmatched) updateRecentChatMessage(match, chatMessage)
                        updateUnread(match)
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
        withContext(Dispatchers.IO) {
            val match = matchMapper.fromDTOToEntity(matchDTO)
            saveMatch(match)
            newMatchFlowListener?.onReceive(matchMapper.fromEntityToProfileTuple(match))
        }
    }

    override suspend fun getMatchInvalidation(): Flow<Boolean> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.invalidation()
        }
    }

    private fun saveMatch(match: Match) {
        updateMatch(match)
        updateRecentChatMessage(match)
        updateUnread(match)
        matchDAO.insert(match)
    }

    companion object {
        const val UNMATCHED = -1L
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
            createDummyChatMessages()
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