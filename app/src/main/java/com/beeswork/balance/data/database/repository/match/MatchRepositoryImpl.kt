package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.listener.ResourceListener
import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.database.response.ChatMessagePagingRefresh
import com.beeswork.balance.data.database.response.MatchPagingRefresh
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
import com.beeswork.balance.data.database.response.NewChatMessage
import com.beeswork.balance.data.database.response.NewMatch
import com.beeswork.balance.data.listener.ChatMessagePagingRefreshListener
import com.beeswork.balance.data.listener.MatchPagingRefreshListener
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.service.stomp.StompClient
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
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
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val clickerDAO: ClickerDAO,
    private val clickedDAO: ClickedDAO,
    private val matchMapper: MatchMapper,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider,
    private val stompClient: StompClient,
    private val scope: CoroutineScope
) : MatchRepository {
    private var matchPagingRefreshListener: MatchPagingRefreshListener? = null
    private var chatMessagePagingRefreshListener: ChatMessagePagingRefreshListener? = null
    private var sendChatMessageListener: ResourceListener<EmptyResponse>? = null

    @ExperimentalCoroutinesApi
    override val matchPagingRefreshFlow = callbackFlow<MatchPagingRefresh> {
        matchPagingRefreshListener = object : MatchPagingRefreshListener {
            override fun onRefresh(matchPagingRefresh: MatchPagingRefresh) {
                offer(matchPagingRefresh)
            }
        }
        awaitClose { }
    }

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
    override val sendChatMessageFlow = callbackFlow<Resource<EmptyResponse>> {
        sendChatMessageListener = object : ResourceListener<EmptyResponse> {
            override fun onInvoke(element: Resource<EmptyResponse>) {
                offer(element)
            }
        }
        awaitClose { }
    }

    init {
        collectChatMessageReceiptFlow()
        collectChatMessageReceivedFlow()
    }

    private fun collectChatMessageReceiptFlow() {
        stompClient.chatMessageReceiptFlow.onEach { chatMessageDTO ->
            safeLet(chatMessageDTO.key, chatMessageDTO.chatId) { key, chatId ->
                chatMessageDTO.id?.let { id ->
                    if (id == UNMATCHED) {
                        chatMessageDAO.updateStatus(key, ChatMessageStatus.ERROR)
                        onMatchUnmatched(chatId)
                    } else {
                        chatMessageDAO.updateStatus(key, ChatMessageStatus.SENT)
                        onChatMessageFetched(key, id, chatMessageDTO.createdAt)
                    }
                } ?: chatMessageDAO.updateStatus(key, ChatMessageStatus.ERROR)

                chatMessagePagingRefreshListener?.onRefresh(
                    ChatMessagePagingRefresh(
                        null,
                        ChatMessagePagingRefresh.Type.FETCHED
                    )
                )
            }
        }.launchIn(scope)
    }

    private fun collectChatMessageReceivedFlow() {
        stompClient.chatMessageReceivedFlow.onEach { chatMessageDTO ->
            val chatMessage = chatMessageMapper.fromDTOToEntity(chatMessageDTO)
            chatMessageDAO.insert(chatMessage)
            matchDAO.findById(chatMessage.chatId)?.let { match ->
                val newChatMessage = NewChatMessage(match.name, match.matchedId, match.repPhotoKey, chatMessage.body)
                chatMessagePagingRefreshListener?.onRefresh(
                    ChatMessagePagingRefresh(
                        newChatMessage,
                        ChatMessagePagingRefresh.Type.RECEIVED
                    )
                )
            }
        }.launchIn(scope)
    }

    private fun onChatMessageFetched(key: Long, id: Long, createdAt: OffsetDateTime?) {
        chatMessageDAO.findByKey(key)?.let { chatMessage ->
            chatMessage.id = id
            chatMessage.createdAt = createdAt
            chatMessageDAO.insert(chatMessage)
            updateMatchActive(chatMessage.chatId)
        }
    }

    private fun updateMatchActive(chatId: Long) {
        matchDAO.findById(chatId)?.let { match ->
            match.active = true
            matchDAO.insert(match)
        }
    }

    private fun onMatchUnmatched(chatId: Long) {
        matchDAO.findById(chatId)?.let { match ->
            match.unmatched = true
            match.updatedAt = null
            match.recentChatMessage = ""
            match.repPhotoKey = null
            match.active = true
            matchDAO.insert(match)
        }
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
            val listMatches = matchRDS.listMatches(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                preferenceProvider.getMatchFetchedAt()
            )
            listMatches.data?.let { data ->
                saveMatches(data.matchDTOs)
                saveChatMessages(data.sentChatMessageDTOs, data.receivedChatMessageDTOs)
                preferenceProvider.putMatchFetchedAt(data.fetchedAt)
                chatMessagePagingRefreshListener?.onRefresh(
                    ChatMessagePagingRefresh(
                        null,
                        ChatMessagePagingRefresh.Type.FETCHED
                    )
                )
                matchPagingRefreshListener?.onRefresh(MatchPagingRefresh(null))
            }
            return@withContext Resource.toEmptyResponse(listMatches)
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
                if (!chatMessageDAO.existById(it.id))
                    chatMessageDAO.insert(it)
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
        val matchedIds = mutableListOf<UUID>()
        val clickedList = mutableListOf<Clicked>()

        balanceDatabase.runInTransaction {
            matches.forEach {
                updateMatch(it)
                matchedIds.add(it.matchedId)
                clickedList.add(Clicked(it.matchedId))
            }

            matchDAO.insert(matches)
            clickerDAO.deleteInIds(matchedIds)
            clickedDAO.insert(clickedList)
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
            matchPagingRefreshListener?.onRefresh(MatchPagingRefresh(null))
        }
    }

    override suspend fun isUnmatched(chatId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.findUnmatched(chatId)
        }
    }

    override suspend fun sendChatMessage(chatId: Long, matchedId: UUID, body: String) {
        withContext(Dispatchers.IO) {
            val key = chatMessageDAO.insert(ChatMessage(chatId, body, ChatMessageStatus.SENDING, null))
            chatMessagePagingRefreshListener?.onRefresh(
                ChatMessagePagingRefresh(
                    null,
                    ChatMessagePagingRefresh.Type.SEND
                )
            )
            stompClient.sendChatMessage(key, chatId, matchedId, body)
        }
    }

    override suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage> {
        return withContext(Dispatchers.IO) {
            return@withContext chatMessageDAO.findAllPaged(loadSize, startPosition, chatId)
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

    companion object {
        const val UNMATCHED = -1L
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
//                    repPhotoKey = "",
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
