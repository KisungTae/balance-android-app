package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.repository.match.MatchRepositoryImpl
import com.beeswork.balance.data.database.response.ChatMessagePagingRefresh
import com.beeswork.balance.data.database.response.NewChatMessage
import com.beeswork.balance.data.listener.ChatMessagePagingRefreshListener
import com.beeswork.balance.data.listener.ResourceListener
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.service.stomp.StompClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import java.util.*

class ChatRepositoryImpl(
    private val chatRDS: ChatRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider,
    private val stompClient: StompClient,
    private val scope: CoroutineScope
) : ChatRepository {

    private var chatMessagePagingRefreshListener: ChatMessagePagingRefreshListener? = null
    private var sendChatMessageListener: ResourceListener<EmptyResponse>? = null

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
                    if (id == MatchRepositoryImpl.UNMATCHED) {
                        chatMessageDAO.updateStatus(key, ChatMessageStatus.ERROR)
                        onMatchUnmatched(chatId)
                    } else {
                        chatMessageDAO.updateStatus(key, ChatMessageStatus.SENT)
                        onChatMessageSent(key, id, chatMessageDTO.createdAt)
                    }
                } ?: chatMessageDAO.updateStatus(key, ChatMessageStatus.ERROR)
                refreshChatMessagePaging(ChatMessagePagingRefresh.Type.SENT, chatId)
            }
        }.launchIn(scope)
    }

    private fun refreshChatMessagePaging(
        type: ChatMessagePagingRefresh.Type,
        chatId: Long,
        newChatMessage: NewChatMessage? = null
    ) {
        chatMessagePagingRefreshListener?.onRefresh(ChatMessagePagingRefresh(type, chatId, newChatMessage))
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

    private fun collectChatMessageFlow() {
        stompClient.chatMessageFlow.onEach { chatMessageDTO ->
            val chatMessage = chatMessageMapper.fromDTOToEntity(chatMessageDTO)
            chatMessageDAO.insert(chatMessage)
            refreshChatMessagePaging(
                ChatMessagePagingRefresh.Type.RECEIVED,
                chatMessage.chatId,
                NewChatMessage(chatMessage.body)
            )
        }.launchIn(scope)
    }

    private fun onChatMessageSent(key: Long, id: Long, createdAt: OffsetDateTime?) {
        chatMessageDAO.findByKey(key)?.let { chatMessage ->
            chatMessage.id = id
            chatMessage.createdAt = createdAt
            chatMessageDAO.insert(chatMessage)

            matchDAO.findById(chatMessage.chatId)?.let { match ->
                match.active = true
                matchDAO.insert(match)
            }
        }
    }

    override suspend fun sendChatMessage(chatId: Long, swipedId: UUID, body: String) {
        withContext(Dispatchers.IO) {
            val key = chatMessageDAO.insert(ChatMessage(chatId, body, ChatMessageStatus.SENDING, OffsetDateTime.now()))
            sendChatMessage(key, chatId, swipedId, body)
        }
    }

    private fun sendChatMessage(key: Long, chatId: Long, swipedId: UUID, body: String) {
        refreshChatMessagePaging(ChatMessagePagingRefresh.Type.SEND, chatId)
        stompClient.sendChatMessage(key, chatId, swipedId, body)
    }

    override suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage> {
        return withContext(Dispatchers.IO) {
            return@withContext chatMessageDAO.findAllPaged(loadSize, startPosition, chatId)
        }
    }

    override suspend fun resendChatMessage(key: Long, swipedId: UUID) {
        withContext(Dispatchers.IO) {
            chatMessageDAO.findByKey(key)?.let {
                chatMessageDAO.updateStatus(it.key, ChatMessageStatus.SENDING)
                sendChatMessage(it.key, it.chatId, swipedId, it.body)
            }
        }
    }

    override suspend fun deleteChatMessage(chatId: Long, key: Long) {
        withContext(Dispatchers.IO) {
            chatMessageDAO.deleteByKey(key)
            refreshChatMessagePaging(ChatMessagePagingRefresh.Type.DELETED, chatId)
        }
    }

    override fun test() {
        refreshChatMessagePaging(
            ChatMessagePagingRefresh.Type.RECEIVED,
            352,
            NewChatMessage("test chat message received")
        )
    }
}