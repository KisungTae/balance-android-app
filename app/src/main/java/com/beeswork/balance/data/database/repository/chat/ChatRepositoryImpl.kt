package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.common.ResourceListener
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.chat.ChatMessageReceiptDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import java.util.*
import kotlin.random.Random

class ChatRepositoryImpl(
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val chatRDS: ChatRDS,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider,
    private val ioDispatcher: CoroutineDispatcher
) : ChatRepository {

    private var chatMessageInvalidationListener: ChatMessageInvalidationListener? = null
    private var chatMessageReceiptFlowListener: ResourceListener<EmptyResponse>? = null

    private var sendChatMessageChanel = Channel<ChatMessageDTO>(Channel.BUFFERED)
    override val sendChatMessageFlow: Flow<ChatMessageDTO> = sendChatMessageChanel.consumeAsFlow()

    @ExperimentalCoroutinesApi
    override val chatMessageInvalidationFlow = callbackFlow<ChatMessageInvalidation> {
        chatMessageInvalidationListener = object : ChatMessageInvalidationListener {
            override fun onInvalidate(chatMessageInvalidation: ChatMessageInvalidation) {
                offer(chatMessageInvalidation)
            }
        }
        awaitClose {}
    }

    @ExperimentalCoroutinesApi
    override val chatMessageReceiptFlow = callbackFlow<Resource<EmptyResponse>> {
        chatMessageReceiptFlowListener = object : ResourceListener<EmptyResponse> {
            override fun onInvoke(element: Resource<EmptyResponse>) {
                offer(element)
            }
        }
        awaitClose {}
    }

    override suspend fun deleteChatMessages() {
        withContext(ioDispatcher) { chatMessageDAO.deleteAll() }
    }

    override suspend fun sendChatMessage(chatId: Long, swipedId: UUID, body: String) {
        withContext(ioDispatcher) {
            val chatMessage = ChatMessage(chatId, body, ChatMessageStatus.SENDING, OffsetDateTime.now())
            val key = chatMessageDAO.insert(chatMessage)
            sendChatMessage(key, chatId, swipedId, body)
        }
    }

//  TODO: accountId to accessToken
    private suspend fun sendChatMessage(key: Long, chatId: Long, swipedId: UUID, body: String) {
//        val chatMessageDTO = ChatMessageDTO(key, chatId, preferenceProvider.getAccountId(), swipedId, body)
//        sendChatMessageChanel.send(chatMessageDTO)
//        chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofSend(chatId))
    }

    override suspend fun resendChatMessage(key: Long, swipedId: UUID) {
        withContext(ioDispatcher) {
            chatMessageDAO.findByKey(key)?.let { chatMessage ->
                chatMessageDAO.updateStatusByKey(chatMessage.key, ChatMessageStatus.SENDING)
                sendChatMessage(chatMessage.key, chatMessage.chatId, swipedId, chatMessage.body)
            }
        }
    }

    override suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage> {
        return withContext(ioDispatcher) {
            return@withContext chatMessageDAO.findAllPaged(loadSize, startPosition, chatId)
        }
    }

    override suspend fun deleteChatMessage(chatId: Long, key: Long) {
        withContext(ioDispatcher) {
            chatMessageDAO.deleteByKey(key)
            chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofDelete(chatId))
        }
    }

    override suspend fun saveChatMessageReceived(chatMessageDTO: ChatMessageDTO) {
        withContext(Dispatchers.IO) {
            chatMessageMapper.toReceivedChatMessage(chatMessageDTO)?.let { chatMessage ->
                chatMessageDAO.insert(chatMessage)
                updateMatchOnNewChatMessage(chatMessage.chatId)
                chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofReceived(chatMessage.chatId, chatMessage.body))
            }
        }
    }

    override suspend fun saveChatMessageReceipt(chatMessageReceiptDTO: ChatMessageReceiptDTO) {
        chatMessageReceiptDTO.id?.let { id ->
            onChatMessageSent(chatMessageReceiptDTO.key, id, chatMessageReceiptDTO.createdAt)
        } ?: kotlin.run {
            if (chatMessageReceiptDTO.error == ExceptionCode.MATCH_UNMATCHED_EXCEPTION) {
                onUnmatchedReceiptReceived(chatMessageReceiptDTO.key, chatMessageReceiptDTO.chatId)
            } else {
                chatMessageDAO.updateStatusByKey(chatMessageReceiptDTO.key, ChatMessageStatus.ERROR)
            }
        }

        chatMessageInvalidationListener?.let { _chatMessageInvalidationListener ->
            val chatId = chatMessageDAO.findChatIdByKey(chatMessageReceiptDTO.key)
            val chatMessageInvalidation = ChatMessageInvalidation.ofReceipt(chatId)
            _chatMessageInvalidationListener.onInvalidate(chatMessageInvalidation)
        }
    }

    private fun onUnmatchedReceiptReceived(key: Long?, chatId: Long?) {
        chatMessageDAO.updateStatusByKey(key, ChatMessageStatus.ERROR)
        matchDAO.updateAsUnmatched(chatId)
        chatMessageReceiptFlowListener?.onInvoke(Resource.error(ExceptionCode.MATCH_UNMATCHED_EXCEPTION))
    }

    private fun onChatMessageSent(key: Long?, id: Long, createdAt: OffsetDateTime?) {
        chatMessageDAO.findByKey(key)?.let { chatMessage ->
            createdAt?.let { _createdAt ->
                chatMessage.id = id
                chatMessage.createdAt = _createdAt
                chatMessage.status = ChatMessageStatus.SENT
                chatMessageDAO.insert(chatMessage)
                updateMatchOnNewChatMessage(chatMessage.chatId)
            }
        }
    }

    override suspend fun fetchChatMessages(): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val fetchedAt = OffsetDateTime.now()
            val response = chatRDS.listChatMessages()

            response.data?.let { data ->
                val chatIds = saveChatMessages(data.sentChatMessageDTOs, data.receivedChatMessageDTOs)
                balanceDatabase.runInTransaction { chatIds.forEach { chatId -> updateMatchOnNewChatMessage(chatId) } }
                chatMessageDAO.updateStatusBefore(fetchedAt, ChatMessageStatus.SENDING, ChatMessageStatus.ERROR)
                chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofFetched())
            }
            return@withContext response.toEmptyResponse()
        }
    }

    override suspend fun connectStomp() {
        withContext(ioDispatcher) {
//            stompClient.connect()
        }
    }


    private fun saveChatMessages(
        sentChatMessageDTOs: List<ChatMessageDTO>?,
        receivedChatMessageDTOs: List<ChatMessageDTO>?
    ): MutableSet<Long> {
        val chatIds = mutableSetOf<Long>()
        val sentChatMessageIds = mutableListOf<Long>()
        val receivedChatMessageIds = mutableListOf<Long>()
        val newChatMessages = mutableListOf<ChatMessage>()

        receivedChatMessageDTOs?.forEach { chatMessageDTO ->
            chatMessageMapper.toReceivedChatMessage(chatMessageDTO)?.let { chatMessage ->
                newChatMessages.add(chatMessage)
                receivedChatMessageIds.add(chatMessage.id)
                chatIds.add(chatMessage.chatId)
            }
        }

        sentChatMessageDTOs?.forEach { chatMessageDTO ->
            chatMessageMapper.toSentChatMessage(
                chatMessageDAO.findByKey(chatMessageDTO.key),
                chatMessageDTO
            )?.let { chatMessage ->
                newChatMessages.add(chatMessage)
                chatIds.add(chatMessage.chatId)
            }
            chatMessageDTO.id?.let { sentChatMessageIds.add(it) }
        }
        syncChatMessages(sentChatMessageIds, receivedChatMessageIds)
        chatMessageDAO.insert(newChatMessages)
        return chatIds
    }

    private fun syncChatMessages(
        sentChatMessageIds: List<Long>,
        receivedChatMessageIds: List<Long>
    ) {
        if (sentChatMessageIds.isEmpty() && receivedChatMessageIds.isEmpty()) return
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { c, t -> }) {
            chatRDS.syncChatMessages(sentChatMessageIds, receivedChatMessageIds)
        }
    }

    private fun updateMatchOnNewChatMessage(chatId: Long) {
        matchDAO.findById(chatId)?.let { match ->
            if (!match.unmatched) chatMessageDAO.findMostRecentAfter(
                match.chatId,
                match.lastReadChatMessageId
            )?.let { chatMessage ->
                match.recentChatMessage = chatMessage.body
                match.updatedAt = chatMessage.createdAt
                match.active = true
                match.unread = chatMessageDAO.existAfter(match.chatId, match.lastReadChatMessageId)
            }
            matchDAO.insert(match)
        }
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

    //    override suspend fun saveChatMessages(
//        sentChatMessagesDTOs: List<ChatMessageDTO>?,
//        receivedChatMessageDTOs: List<ChatMessageDTO>?,
//        fetchedAt: OffsetDateTime
//    ) {
//        withContext(ioDispatcher) {
//            val chatIds = saveChatMessages(sentChatMessagesDTOs, receivedChatMessageDTOs)
//            balanceDatabase.runInTransaction { chatIds.forEach { chatId -> updateMatchOnNewChatMessage(chatId) } }
//            chatMessageDAO.updateStatusBefore(fetchedAt, ChatMessageStatus.SENDING, ChatMessageStatus.ERROR)
//            chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofFetched())
//        }
//    }

    override fun test() {
//        invalidateChatMessages(
//            ChatMessageInvalidation.Type.RECEIVED,
//            352,
//            "test chat message received"
//        )

    }
}


//TODO: move logic from match to chat
//TODO: change logic savechatmessage and savematches and order
//TODO: receive and sent chatmessage should update the recent chatmessage
//TODO: check runinTransaction in chat, match, click repositories