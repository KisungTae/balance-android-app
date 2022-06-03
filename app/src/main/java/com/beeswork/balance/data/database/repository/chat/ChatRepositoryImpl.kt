package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.common.CallBackFlowListener
import com.beeswork.balance.data.database.common.PageFetchDateTracker
import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchCountDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.database.repository.BaseRepository
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.rds.login.LoginRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.chat.StompReceiptDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.data.network.service.stomp.WebSocketEvent
import com.beeswork.balance.data.network.service.stomp.WebSocketStatus
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.exception.ChatMessageNotFoundException
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.threeten.bp.OffsetDateTime
import java.util.*


@ExperimentalCoroutinesApi
class ChatRepositoryImpl(
    loginRDS: LoginRDS,
    preferenceProvider: PreferenceProvider,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val matchCountDAO: MatchCountDAO,
    private val chatRDS: ChatRDS,
    private val stompClient: StompClient,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val applicationScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher
) : BaseRepository(loginRDS, preferenceProvider), ChatRepository {

    private val chatPageFetchDateTracker = PageFetchDateTracker(30L)

    private var chatPageCallBackFlowListener: CallBackFlowListener<ChatMessage?>? = null
    override val chatPageInvalidationFlow: Flow<ChatMessage?> = callbackFlow {
        chatPageCallBackFlowListener = object : CallBackFlowListener<ChatMessage?> {
            override fun onInvoke(data: ChatMessage?) {
                offer(data)
            }
        }
        awaitClose { }
    }

    override fun getWebSocketEventFlow(): SharedFlow<WebSocketEvent> {
        return stompClient.webSocketEventFlow
    }

    init {
        stompClient.webSocketEventFlow.onEach { webSocketEvent ->
            // todo: implement page refresh logic
        }.launchIn(applicationScope)

        stompClient.stompReceiptFlow.onEach { stompReceiptDTO ->
            saveChatMessageReceipt(stompReceiptDTO)
        }.launchIn(applicationScope)

        stompClient.chatMessageFlow.onEach { chatMessageDTO ->
            saveChatMessage(chatMessageDTO)
        }.launchIn(applicationScope)
    }

    override suspend fun sendChatMessage(chatId: UUID, body: String): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val chatMessage = ChatMessage(chatId, body, ChatMessageStatus.SENDING, UUID.randomUUID())
            chatMessageDAO.insert(chatMessage)
            val chatMessageDTO = ChatMessageDTO(chatMessage.chatId, chatMessage.tag, chatMessage.body)
            val response = stompClient.sendChatMessage(chatMessageDTO)
            if (response.isError()) {
                chatMessageDAO.updateStatusBy(chatMessage.tag, ChatMessageStatus.ERROR)
            }
            chatPageCallBackFlowListener?.onInvoke(chatMessage)
            return@withContext response
        }
    }

    override suspend fun resendChatMessage(tag: UUID): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val chatMessage = chatMessageDAO.getBy(tag) ?: return@withContext Resource.error(ChatMessageNotFoundException())
            val chatMessageDTO = ChatMessageDTO(chatMessage.chatId, chatMessage.tag, chatMessage.body)
            val response = stompClient.sendChatMessage(chatMessageDTO)
            if (response.isSuccess()) {
                chatMessageDAO.updateStatusBy(chatMessage.tag, ChatMessageStatus.SENDING)
                chatPageCallBackFlowListener?.onInvoke(null)
            }
            return@withContext response
        }
    }

    override suspend fun fetchChatMessages(chatId: UUID, lastChatMessageId: Long?, loadSize: Int): Resource<List<ChatMessageDTO>> {
        return withContext(ioDispatcher) {
            val response = getResponse { chatRDS.fetchChatMessages(chatId, lastChatMessageId, loadSize) }
            if (response.data != null && response.data.isNotEmpty()) {
                val isUpdated = saveChatMessages(response.data)
                if (lastChatMessageId == null && isUpdated) {
                    updateLastChatMessageOnMatch(chatId)
                }
                syncChatMessages(chatId, response.data)
            }
            return@withContext response
        }
    }

    private fun saveChatMessages(chatMessageDTOs: List<ChatMessageDTO>?): Boolean {
        var isUpdated = false
        balanceDatabase.runInTransaction {
            val accountId = preferenceProvider.getAccountId()
            chatMessageDTOs?.forEach { chatMessageDTO ->
                val chatMessage = insertChatMessage(accountId, chatMessageDTO)
                if (chatMessage != null) {
                    isUpdated = true
                }
            }
            if (isUpdated) {
                chatPageCallBackFlowListener?.onInvoke(null)
            }
        }
        return isUpdated
    }

    private fun insertChatMessage(accountId: UUID?, chatMessageDTO: ChatMessageDTO): ChatMessage? {
        if (chatMessageDTO.id == null || chatMessageDTO.senderId == null) {
            return null
        }

        val chatMessage = if (chatMessageDTO.senderId == accountId) {
            chatMessageDAO.getBy(chatMessageDTO.tag)
        } else {
            chatMessageDAO.getById(chatMessageDTO.id, chatMessageDTO.chatId)
        }

        if (chatMessage == null || !chatMessage.isEqualTo(chatMessageDTO)) {
            val status = if (chatMessageDTO.senderId == accountId) {
                ChatMessageStatus.SENT
            } else {
                ChatMessageStatus.RECEIVED
            }
            val newChatMessage = chatMessageMapper.toChatMessage(chatMessageDTO, status, chatMessage?.sequence)
            chatMessageDAO.insert(newChatMessage)
            return newChatMessage
        }
        return null
    }

    override suspend fun loadChatMessages(chatId: UUID, startPosition: Int, loadSize: Int): List<ChatMessage> {
        return withContext(ioDispatcher) {
            if (chatPageFetchDateTracker.shouldFetchPage(getChatPageFetchDateTrackerKey(chatId, startPosition))) {
                listChatMessages(chatId, startPosition, loadSize)
            }
            return@withContext chatMessageDAO.getAllPagedBy(chatId, startPosition, loadSize)
        }
    }

    private fun listChatMessages(chatId: UUID, startPosition: Int, loadSize: Int) {
        applicationScope.launch(CoroutineExceptionHandler { _, _ -> }) {
            chatPageFetchDateTracker.updateFetchDate(getChatPageFetchDateTrackerKey(chatId, startPosition), OffsetDateTime.now())
            val response = getResponse { chatRDS.listChatMessages(chatId, preferenceProvider.getAppToken(), startPosition, loadSize) }
            if (response.isError()) {
                chatPageFetchDateTracker.updateFetchDate(getChatPageFetchDateTrackerKey(chatId, startPosition), null)
            }
            if (response.data != null && response.data.isNotEmpty()) {
                val isUpdated = saveChatMessages(response.data)
                if (startPosition == 0 && isUpdated) {
                    updateLastChatMessageOnMatch(chatId)
                }
                syncChatMessages(chatId, response.data)
            }
        }
    }

    private fun syncChatMessages(chatId: UUID, chatMessageDTOs: List<ChatMessageDTO>) {
        applicationScope.launch(CoroutineExceptionHandler { _, _ -> }) {
            val chatMessageIds = arrayListOf<Long>()
            chatMessageDTOs.forEach { chatMessageDTO ->
                if (chatMessageDTO.id != null) {
                    chatMessageIds.add(chatMessageDTO.id)
                }
            }
            chatRDS.syncChatMessages(chatId, preferenceProvider.getAppToken(), chatMessageIds)
        }
    }

    private fun updateLastChatMessageOnMatch(chatId: UUID?) {
        balanceDatabase.runInTransaction {
            val match = matchDAO.getBy(chatId) ?: return@runInTransaction
            val lastChatMessage = chatMessageDAO.getLastChatMessageBy(match.chatId) ?: return@runInTransaction

            if (lastChatMessage.id > match.lastChatMessageId) {
                match.lastChatMessageId = lastChatMessage.id
                match.lastChatMessageBody = lastChatMessage.body
                match.lastChatMessageCreatedAt = lastChatMessage.createdAt
            }

            val lastReceivedChatMessageId = if (lastChatMessage.status == ChatMessageStatus.RECEIVED) {
                lastChatMessage.id
            } else {
                chatMessageDAO.getLastReceivedChatMessageId(match.chatId) ?: 0
            }

            if (lastReceivedChatMessageId > match.lastReceivedChatMessageId) {
                match.lastReceivedChatMessageId = lastReceivedChatMessageId
            }
            matchDAO.insert(match)
        }
    }

    private fun decrementMatchCount(updatedAt: OffsetDateTime) {
        balanceDatabase.runInTransaction {
            val matchCount = matchCountDAO.getBy(preferenceProvider.getAccountId())
            if (matchCount != null && updatedAt.isAfter(matchCount.countedAt) && matchCount.count > 0) {
                matchCount.count = matchCount.count - 1
                matchCountDAO.insert(matchCount)
            }
        }
    }

    private fun sendChatMessages() {
        applicationScope.launch(CoroutineExceptionHandler { _, _ -> }) {
            val chatMessages = chatMessageDAO.getAllBy(ChatMessageStatus.SENDING)
            for (chatMessage in chatMessages) {
                val chatMessageDTO = ChatMessageDTO(chatMessage.chatId, chatMessage.tag, chatMessage.body)
                stompClient.sendChatMessage(chatMessageDTO)
            }
        }
    }

    private suspend fun saveChatMessageReceipt(stompReceiptDTO: StompReceiptDTO) {
        withContext(ioDispatcher) {
            if (stompReceiptDTO.id != null && stompReceiptDTO.createdAt != null) {
                chatMessageDAO.updateAsSentBy(stompReceiptDTO.tag, stompReceiptDTO.id, stompReceiptDTO.createdAt)
                val chatId = chatMessageDAO.getChatIdBy(stompReceiptDTO.tag)
                updateLastChatMessageOnMatch(chatId)
                if (stompReceiptDTO.firstMessage == true) {
                    decrementMatchCount(stompReceiptDTO.createdAt)
                }
            } else if (stompReceiptDTO.error == ExceptionCode.MATCH_UNMATCHED_EXCEPTION) {
                chatMessageDAO.updateStatusBy(stompReceiptDTO.tag, ChatMessageStatus.ERROR)
                val chatId = chatMessageDAO.getChatIdBy(stompReceiptDTO.tag)
                matchDAO.updateAsUnmatched(chatId)
            } else if (stompReceiptDTO.error != null) {
                chatMessageDAO.updateStatusBy(stompReceiptDTO.tag, ChatMessageStatus.ERROR)
            }
            if (stompReceiptDTO.tag != null) {
                chatPageCallBackFlowListener?.onInvoke(null)
            }
        }
    }

    private fun getChatPageFetchDateTrackerKey(chatId: UUID, startPosition: Int): String {
        return "$chatId$startPosition"
    }

    override suspend fun saveChatMessage(chatMessageDTO: ChatMessageDTO) {
        withContext(ioDispatcher) {
            val chatMessage = insertChatMessage(preferenceProvider.getAccountId(), chatMessageDTO)
            if (chatMessage != null) {
                updateLastChatMessageOnMatch(chatMessageDTO.chatId)
                chatPageCallBackFlowListener?.onInvoke(chatMessage)
            }
            if (chatMessageDTO.firstMessage == true && chatMessageDTO.createdAt != null) {
                decrementMatchCount(chatMessageDTO.createdAt)
            }
        }
    }

    override suspend fun deleteChatMessage(chatId: UUID, tag: UUID) {
        withContext(ioDispatcher) {
            chatMessageDAO.deleteBy(chatId, tag)
            chatPageCallBackFlowListener?.onInvoke(null)
        }
    }

    override suspend fun deleteChatMessages() {
        withContext(ioDispatcher) { chatMessageDAO.deleteAll() }
    }




    override fun test() {

    }
}


//TODO: move logic from match to chat
//TODO: change logic savechatmessage and savematches and order
//TODO: receive and sent chatmessage should update the recent chatmessage
//TODO: check runinTransaction in chat, match, click repositories