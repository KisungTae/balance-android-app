package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.common.ResourceListener
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.StompHeader
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.OffsetDateTime
import java.util.*

class ChatRepositoryImpl(
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val chatMessageMapper: ChatMessageMapper,
    private val stompClient: StompClient,
    private val balanceDatabase: BalanceDatabase,
    private val scope: CoroutineScope
) : ChatRepository {

    private var chatMessageInvalidationListener: ChatMessageInvalidationListener? = null
    private var sendChatMessageListener: ResourceListener<EmptyResponse>? = null

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
    override val sendChatMessageFlow = callbackFlow<Resource<EmptyResponse>> {
        sendChatMessageListener = object : ResourceListener<EmptyResponse> {
            override fun onInvoke(element: Resource<EmptyResponse>) {
                offer(element)
            }
        }
        awaitClose {}
    }

    init {
        collectChatMessageReceiptFlow()
        collectChatMessageFlow()
    }

    private fun collectChatMessageReceiptFlow() {
        stompClient.chatMessageReceiptFlow.onEach { chatMessageDTO ->
            safeLet(chatMessageDTO.key, chatMessageDTO.chatId) { key, chatId ->
                chatMessageDTO.id?.let { id ->
                    if (id == StompHeader.UNMATCHED_RECEIPT_ID) {
                        chatMessageDAO.updateStatus(key, ChatMessageStatus.ERROR)
                        onMatchUnmatched(chatId)
                        sendChatMessageListener?.onInvoke(Resource.error(ExceptionCode.MATCH_UNMATCHED_EXCEPTION))
                    } else onChatMessageSent(key, id, chatMessageDTO.createdAt)
                } ?: chatMessageDAO.updateStatus(key, ChatMessageStatus.ERROR)
                invalidateChatMessages(ChatMessageInvalidation.Type.RECEIPT, chatId)
            }
        }.launchIn(scope)
    }

    private fun collectChatMessageFlow() {
        stompClient.chatMessageFlow.onEach { chatMessageDTO ->
            saveChatMessageReceived(chatMessageMapper.fromDTOToEntity(chatMessageDTO))
        }.launchIn(scope)
    }

    private fun invalidateChatMessages(
        type: ChatMessageInvalidation.Type,
        chatId: Long,
        body: String? = null
    ) {
        chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation(type, chatId, body))
    }

    private fun onMatchUnmatched(chatId: Long) {
        matchDAO.findById(chatId)?.let { match ->
            match.unmatched = true
            match.updatedAt = null
            match.recentChatMessage = ""
            match.profilePhotoKey = null
            match.active = true
            matchDAO.insert(match)
        }
    }

    private fun onChatMessageSent(key: Long, id: Long, createdAt: OffsetDateTime?) {
        chatMessageDAO.findByKey(key)?.let { chatMessage ->
            chatMessage.id = id
            chatMessage.createdAt = createdAt
            chatMessage.status = ChatMessageStatus.SENT
            chatMessageDAO.insert(chatMessage)
            updateRecentChatMessage(chatMessage)
        }
    }

    private fun updateRecentChatMessage(chatMessage: ChatMessage) {
        balanceDatabase.runInTransaction {

        }
    }

    override suspend fun sendChatMessage(chatId: Long, swipedId: UUID, body: String) {
        withContext(Dispatchers.IO) {
            val key = chatMessageDAO.insert(ChatMessage(chatId, body, ChatMessageStatus.SENDING, OffsetDateTime.now()))
            sendChatMessage(key, chatId, swipedId, body)
        }
    }

    private fun sendChatMessage(key: Long, chatId: Long, swipedId: UUID, body: String) {
        invalidateChatMessages(ChatMessageInvalidation.Type.SEND, chatId)
        stompClient.sendChatMessage(key, chatId, swipedId, body)
    }

    override suspend fun resendChatMessage(key: Long, swipedId: UUID) {
        withContext(Dispatchers.IO) {
            chatMessageDAO.findByKey(key)?.let {
                chatMessageDAO.updateStatus(it.key, ChatMessageStatus.SENDING)
                sendChatMessage(it.key, it.chatId, swipedId, it.body)
            }
        }
    }

    override suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage> {
        return withContext(Dispatchers.IO) {
            return@withContext chatMessageDAO.findAllPaged(loadSize, startPosition, chatId)
        }
    }

    override suspend fun deleteChatMessage(chatId: Long, key: Long) {
        withContext(Dispatchers.IO) {
            chatMessageDAO.deleteByKey(key)
            invalidateChatMessages(ChatMessageInvalidation.Type.DELETED, chatId)
        }
    }

    override suspend fun saveChatMessageReceived(chatMessageDTO: ChatMessageDTO) {
        withContext(Dispatchers.IO) {
            saveChatMessageReceived(chatMessageMapper.fromDTOToEntity(chatMessageDTO))
        }
    }

    private fun saveChatMessageReceived(chatMessage: ChatMessage) {
        chatMessageDAO.insert(chatMessage)
        updateRecentChatMessage(chatMessage)
        invalidateChatMessages(
            ChatMessageInvalidation.Type.RECEIVED,
            chatMessage.chatId,
            chatMessage.body
        )
    }


    override fun test() {
        invalidateChatMessages(
            ChatMessageInvalidation.Type.RECEIVED,
            352,
            "test chat message received"
        )
    }
}


//TODO: move logic from match to chat
//TODO: change logic savechatmessage and savematches and order
//TODO: receive and sent chatmessage should update the recent chatmessage
//TODO: check runinTransaction in chat, match, click repositories