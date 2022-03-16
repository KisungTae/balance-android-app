package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.common.InvalidationListener
import com.beeswork.balance.data.database.common.PageFetchDateTracker
import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.database.repository.BaseRepository
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.rds.login.LoginRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.chat.ChatMessageReceiptDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.exception.ChatMessageNotFoundException
import com.beeswork.balance.internal.exception.ChatNotFoundException
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.threeten.bp.OffsetDateTime
import java.util.*
import kotlin.random.Random

class ChatRepositoryImpl(
    loginRDS: LoginRDS,
    preferenceProvider: PreferenceProvider,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val chatRDS: ChatRDS,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : BaseRepository(loginRDS, preferenceProvider), ChatRepository {

    private var sendChatMessageChanel = Channel<ChatMessageDTO>(Channel.BUFFERED)
    override val sendChatMessageFlow: Flow<ChatMessageDTO> = sendChatMessageChanel.consumeAsFlow()

    private lateinit var chatPageInvalidationListener: InvalidationListener<ChatMessage?>
    private val chatPageFetchDateTracker = PageFetchDateTracker(30L)

    @ExperimentalCoroutinesApi
    override val chatPageInvalidationFlow: Flow<ChatMessage?> = callbackFlow {
        chatPageInvalidationListener = object : InvalidationListener<ChatMessage?> {
            override fun onInvalidate(data: ChatMessage?) {
                offer(data)
            }
        }
        awaitClose { }
    }

    override suspend fun sendChatMessage(chatId: UUID, body: String): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val chatMessage = ChatMessage(chatId, body, ChatMessageStatus.SENDING, UUID.randomUUID())
            chatMessageDAO.insert(chatMessage)
            val chatMessageDTO = ChatMessageDTO(null, chatMessage.chatId, null, chatMessage.tag, chatMessage.body, null)
//        sendChatMessageChanel.send(chatMessageDTO)
            chatPageInvalidationListener.onInvalidate(chatMessage)
            return@withContext Resource.success(EmptyResponse())
        }
    }

    override suspend fun resendChatMessage(tag: UUID): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val chatMessage = chatMessageDAO.getBy(tag) ?: return@withContext Resource.error(ChatMessageNotFoundException())
            chatMessageDAO.updateStatusBy(chatMessage.tag, ChatMessageStatus.SENDING)
            val chatMessageDTO = ChatMessageDTO(null, chatMessage.chatId, null, chatMessage.tag, chatMessage.body, null)
//        sendChatMessageChanel.send(chatMessageDTO)
            chatPageInvalidationListener.onInvalidate(null)
            return@withContext Resource.success(EmptyResponse())
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
            chatMessageDTOs?.forEach { chatMessageDTO ->
                val chatMessage = insertChatMessage(preferenceProvider.getAccountId(), chatMessageDTO)
                if (chatMessage != null) {
                    isUpdated = true
                }
            }
            if (isUpdated) {
                chatPageInvalidationListener.onInvalidate(null)
            }
        }
        return isUpdated
    }

    private fun insertChatMessage(senderId: UUID?, chatMessageDTO: ChatMessageDTO): ChatMessage? {
        if (chatMessageDTO.id == null || chatMessageDTO.senderId == null) {
            return null
        }

        val chatMessage = if (chatMessageDTO.senderId == senderId) {
            chatMessageDAO.getBy(chatMessageDTO.tag)
        } else {
            chatMessageDAO.getById(chatMessageDTO.id, chatMessageDTO.chatId)
        }

        if (chatMessage == null || !chatMessage.isEqualTo(chatMessageDTO)) {
            val status = if (chatMessageDTO.senderId == senderId) {
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
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { _, _ -> }) {
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
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { _, _ -> }) {
            val chatMessageIds = arrayListOf<Long>()
            chatMessageDTOs.forEach { chatMessageDTO ->
                if (chatMessageDTO.id != null) {
                    chatMessageIds.add(chatMessageDTO.id)
                }
            }
            chatRDS.syncChatMessages(chatId, preferenceProvider.getAppToken(), chatMessageIds)
        }
    }

    private fun updateLastChatMessageOnMatch(chatId: UUID) {
        balanceDatabase.runInTransaction {
            val match = matchDAO.getBy(chatId)
            if (match != null) {
                val lastChatMessage = chatMessageDAO.getLastChatMessageBy(match.chatId)
                if (lastChatMessage != null && lastChatMessage.id > match.lastChatMessageId) {
                    matchDAO.updateLastChatMessageBy(match.chatId, lastChatMessage.id, lastChatMessage.body)
                }
            }
        }
    }

    private fun getChatPageFetchDateTrackerKey(chatId: UUID, startPosition: Int): String {
        return "$chatId$startPosition"
    }











    override suspend fun deleteChatMessages() {
        withContext(ioDispatcher) { chatMessageDAO.deleteAll() }
    }


    override suspend fun deleteChatMessage(chatId: Long, key: Long) {
        withContext(ioDispatcher) {
            chatMessageDAO.deleteByKey(key)
//            chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofDelete(chatId))
        }
    }

    override suspend fun saveChatMessageReceived(chatMessageDTO: ChatMessageDTO) {
//        withContext(ioDispatcher) {
//            chatMessageMapper.toReceivedChatMessage(chatMessageDTO)?.let { chatMessage ->
//                chatMessageDAO.insert(chatMessage)
//                CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { c, t -> }) {
////                    chatRDS.receivedChatMessage(chatMessage.id)
//                }
////                updateMatchOnNewChatMessage(chatMessage.chatId)
////                chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofReceived(chatMessage.chatId, chatMessage.body))
//            }
//        }
    }

    override suspend fun saveChatMessageReceipt(chatMessageReceiptDTO: ChatMessageReceiptDTO) {
//        withContext(ioDispatcher) {
//            var chatId: Long? = null
//            safeLet(chatMessageReceiptDTO.createdAt, chatMessageDAO.getById(chatMessageReceiptDTO.id)) { createdAt, chatMessage ->
//                chatMessage.createdAt = createdAt
//                chatMessage.status = ChatMessageStatus.SENT
//                chatMessageDAO.insert(chatMessage)
//                updateMatchOnNewChatMessage(chatMessage.chatId)
//                chatId = chatMessage.chatId

//                CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { c, t -> }) {
//                    chatRDS.fetchedChatMessage(chatMessage.id)
//                }
//            } ?: kotlin.run {
//                chatId = chatMessageDAO.findChatIdById(chatMessageReceiptDTO.id)
//                if (chatMessageReceiptDTO.error == ExceptionCode.MATCH_UNMATCHED_EXCEPTION) {
//                    chatMessageDAO.updateStatusBy(chatMessageReceiptDTO.id, ChatMessageStatus.ERROR)
//                    todo: implement udpate match as unmatched
//                    matchDAO.updateAsUnmatched(chatId)
//                    chatMessageReceiptFlowListener?.onInvoke(
//                        Resource.error(ServerException(chatMessageReceiptDTO.error, chatMessageReceiptDTO.body))
//                    )
//                } else {
//                    chatMessageDAO.updateStatusBy(chatMessageReceiptDTO.id, ChatMessageStatus.ERROR)
//                }
//            }

//            chatMessageInvalidationListener?.let { _chatMessageInvalidationListener ->
//                val chatMessageInvalidation = ChatMessageInvalidation.ofReceipt(chatId)
//                _chatMessageInvalidationListener.onInvalidate(chatMessageInvalidation)
//            }
//        }
    }


    override suspend fun clearChatMessages() {
        withContext(ioDispatcher) {
            chatMessageDAO.updateStatus(ChatMessageStatus.SENDING, ChatMessageStatus.ERROR)
//            chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofFetched())
        }
    }

    override suspend fun clearChatMessages(chatMessageIds: List<UUID>) {
        withContext(ioDispatcher) {
            chatMessageDAO.updateStatusByIds(chatMessageIds, ChatMessageStatus.ERROR)
//            chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofFetched())
        }
    }

    override suspend fun clearChatMessage(chatMessageId: UUID?) {
        withContext(ioDispatcher) {
//            chatMessageDAO.updateStatusBy(chatMessageId, ChatMessageStatus.ERROR)
//            chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofFetched())
        }
    }

    private fun saveChatMessages(
        sentChatMessageDTOs: List<ChatMessageDTO>?,
        receivedChatMessageDTOs: List<ChatMessageDTO>?
    ): MutableSet<Long> {
        val chatIds = mutableSetOf<Long>()
//        val sentChatMessageIds = mutableListOf<UUID>()
//        val receivedChatMessageIds = mutableListOf<UUID>()
//        val newChatMessages = mutableListOf<ChatMessage>()

//        receivedChatMessageDTOs?.forEach { chatMessageDTO ->
//            if (!chatMessageDAO.existsById(chatMessageDTO.id)) {
//                chatMessageMapper.toReceivedChatMessage(chatMessageDTO)?.let { chatMessage ->
//                    newChatMessages.add(chatMessage)
//                    chatIds.add(chatMessage.chatId)
//                }
//            }
//            chatMessageDTO.id?.let { id -> receivedChatMessageIds.add(id) }
//        }

//        sentChatMessageDTOs?.forEach { chatMessageDTO ->
//            safeLet(chatMessageDTO.createdAt, chatMessageDAO.getById(chatMessageDTO.id)) { createdAt, chatMessage ->
//                chatMessage.createdAt = createdAt
//                chatMessage.status = ChatMessageStatus.SENT
//                newChatMessages.add(chatMessage)
//                chatIds.add(chatMessage.chatId)
//            }
//            chatMessageDTO.id?.let { id -> sentChatMessageIds.add(id) }
//        }
//        syncChatMessages(sentChatMessageIds, receivedChatMessageIds)
//        chatMessageDAO.insert(newChatMessages)
        return chatIds
    }

    private fun listChatMessages(
        sentChatMessageIds: List<UUID>,
        receivedChatMessageIds: List<UUID>
    ) {
        if (sentChatMessageIds.isEmpty() && receivedChatMessageIds.isEmpty()) return
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { _, _ -> }) {
//            chatRDS.syncChatMessages(, sentChatMessageIds)
        }
    }

    private fun updateMatchOnNewChatMessage(chatId: Long) {
//        matchDAO.findById(chatId)?.let { match ->
//            if (!match.unmatched) chatMessageDAO.findMostRecentAfter(
//                match.chatId,
//                match.lastReadChatMessageKey
//            )?.let { chatMessage ->
//                match.recentChatMessage = chatMessage.body
//                match.updatedAt = chatMessage.createdAt
//                match.active = true
//                match.unread = chatMessageDAO.existAfter(match.chatId, match.lastReadChatMessageKey)
//            }
//            matchDAO.insert(match)
//        }
    }

    //  TODO: remove me
    private fun saveSentChatMessages(sentChatMessages: List<ChatMessage>, matches: List<Match>) {
        val chatIds = matches.map { it.chatId }
        for (msg in sentChatMessages) {
            val randomIndex = Random.nextInt(0, chatIds.size - 1)
//            chatMessageDAO.insert(
//                ChatMessage(
//                    chatIds[randomIndex],
//                    "message-${Random.nextFloat()}",
//                    ChatMessageStatus.SENDING,
//                    OffsetDateTime.now(ZoneOffset.UTC),
//                    msg.key,
//                    msg.id,
//                )
//            )
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
//        CoroutineScope(ioDispatcher).launch {
//            val chatMessages = arrayListOf<ChatMessage>()
//            chatMessages.add(ChatMessage(12, "sent-1", ChatMessageStatus.SENDING, UUID.fromString("938249ad-0ffc-46cf-bd7d-dc4b28f1726b")))
//            chatMessages.add(ChatMessage(12, "sent-2", ChatMessageStatus.SENDING, UUID.fromString("cc00800c-e74f-4dac-bd9b-f0ef487e7d9f")))
//            for (i in 0..10) {
//                chatMessageDAO.insert(ChatMessage(UUID.fromString("233dde32-4bc7-4d05-b695-467fff023976"), "message-$i", ChatMessageStatus.RECEIVED, UUID.randomUUID(), OffsetDateTime.now()))
//                chatMessageDAO.insert(ChatMessage(UUID.fromString("233dde32-4bc7-4d05-b695-467fff023976"), "message-$i", ChatMessageStatus.SENT, UUID.randomUUID(), OffsetDateTime.now()))
//            }
//            chatMessageDAO.insert(chatMessages)
//        }


        CoroutineScope(ioDispatcher).launch {
            val today = OffsetDateTime.now()
            val chatMessage = ChatMessage(
                UUID.fromString("233dde32-4bc7-4d05-b695-467fff023976"),
                today.toString(),
                ChatMessageStatus.RECEIVED,
                UUID.randomUUID(),
                today
            )
            chatMessageDAO.insert(chatMessage)
            chatPageInvalidationListener.onInvalidate(chatMessage)
//            val today = OffsetDateTime.now()
//            val chatMessages = arrayListOf<ChatMessage>()
//
//            for (i in 0..30) {
//                var date = today.plusDays(i.toLong())
//
//                for (j in 0..Random.nextInt(0, 15)) {
//                    if (Random.nextBoolean()) {
//                        date = date.plusMinutes(Random.nextLong(0, 3))
//                    }
//
//                    if (Random.nextBoolean()) {
//                        chatMessages.add(
//                            ChatMessage(
//                                UUID.fromString("233dde32-4bc7-4d05-b695-467fff023976"),
//                                date.toString(),
//                                ChatMessageStatus.SENT,
//                                UUID.randomUUID(),
//                                date,
//                                i.toLong()
//                            )
//                        )
//                    } else {
//                        chatMessages.add(
//                            ChatMessage(
//                                UUID.fromString("233dde32-4bc7-4d05-b695-467fff023976"),
//                                date.toString(),
//                                ChatMessageStatus.RECEIVED,
//                                UUID.randomUUID(),
//                                date,
//                                i.toLong()
//                            )
//                        )
//                    }
//                }
//            }
//
//            for (i in 0..10) {
//                chatMessages.add(
//                    ChatMessage(
//                        UUID.fromString("233dde32-4bc7-4d05-b695-467fff023976"),
//                        "sending-$i",
//                        ChatMessageStatus.SENDING,
//                        UUID.randomUUID()
//                    )
//                )
//            }
//            chatMessageDAO.insert(chatMessages)
        }
    }
}


//TODO: move logic from match to chat
//TODO: change logic savechatmessage and savematches and order
//TODO: receive and sent chatmessage should update the recent chatmessage
//TODO: check runinTransaction in chat, match, click repositories