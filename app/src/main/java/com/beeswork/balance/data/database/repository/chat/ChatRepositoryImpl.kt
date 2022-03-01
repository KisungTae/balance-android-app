package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.database.common.ResourceListener
import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.chat.ChatMessageReceiptDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.ServerException
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.util.safeLet
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.threeten.bp.OffsetDateTime
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

    override suspend fun sendChatMessage(chatId: Long, swipedId: UUID, body: String): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
            val chatMessage = ChatMessage(chatId, body, ChatMessageStatus.SENDING, UUID.randomUUID())
            chatMessageDAO.insert(chatMessage)
            sendChatMessage(chatMessageMapper.toChatMessageDTO(chatMessage, accountId, swipedId))
            return@withContext Resource.success(EmptyResponse())
        }
    }

    private suspend fun sendChatMessage(chatMessageDTO: ChatMessageDTO) {
        sendChatMessageChanel.send(chatMessageDTO)
        chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofSend(chatMessageDTO.chatId))
    }

    override suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage> {
        return withContext(ioDispatcher) {
            return@withContext chatMessageDAO.findAllPaged(loadSize, startPosition, chatId)
        }
    }

    override suspend fun resendChatMessage(chatMessageId: UUID?): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
            chatMessageDAO.findChatMessageToSendTupleById(chatMessageId)?.let { chatMessageToSendTuple ->
                chatMessageDAO.updateStatusById(chatMessageToSendTuple.id, ChatMessageStatus.SENDING)
                sendChatMessage(chatMessageMapper.toChatMessageDTO(chatMessageToSendTuple, accountId))
            }
            return@withContext Resource.success(EmptyResponse())
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
                CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { c, t -> }) {
                    chatRDS.receivedChatMessage(chatMessage.id)
                }
                updateMatchOnNewChatMessage(chatMessage.chatId)
                chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofReceived(chatMessage.chatId, chatMessage.body))
            }
        }
    }

    override suspend fun saveChatMessageReceipt(chatMessageReceiptDTO: ChatMessageReceiptDTO) {
        withContext(ioDispatcher) {
            var chatId: Long? = null
            safeLet(chatMessageReceiptDTO.createdAt, chatMessageDAO.findById(chatMessageReceiptDTO.id)) { createdAt, chatMessage ->
                chatMessage.createdAt = createdAt
                chatMessage.status = ChatMessageStatus.SENT
                chatMessageDAO.insert(chatMessage)
                updateMatchOnNewChatMessage(chatMessage.chatId)
                chatId = chatMessage.chatId

                CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { c, t -> }) {
                    chatRDS.fetchedChatMessage(chatMessage.id)
                }
            } ?: kotlin.run {
                chatId = chatMessageDAO.findChatIdById(chatMessageReceiptDTO.id)
                if (chatMessageReceiptDTO.error == ExceptionCode.MATCH_UNMATCHED_EXCEPTION) {
                    chatMessageDAO.updateStatusById(chatMessageReceiptDTO.id, ChatMessageStatus.ERROR)
//                    todo: implement udpate match as unmatched
//                    matchDAO.updateAsUnmatched(chatId)
                    chatMessageReceiptFlowListener?.onInvoke(
                        Resource.error(ServerException(chatMessageReceiptDTO.error, chatMessageReceiptDTO.body))
                    )
                } else {
                    chatMessageDAO.updateStatusById(chatMessageReceiptDTO.id, ChatMessageStatus.ERROR)
                }
            }

            chatMessageInvalidationListener?.let { _chatMessageInvalidationListener ->
                val chatMessageInvalidation = ChatMessageInvalidation.ofReceipt(chatId)
                _chatMessageInvalidationListener.onInvalidate(chatMessageInvalidation)
            }
        }
    }



    override suspend fun fetchChatMessages(): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val fetchedAt = OffsetDateTime.now()
            val response = chatRDS.listChatMessages()

            response.data?.let { data ->
                val chatIds = saveChatMessages(data.sentChatMessageDTOs, data.receivedChatMessageDTOs)
                balanceDatabase.runInTransaction {
                    chatIds.forEach { chatId ->
                        updateMatchOnNewChatMessage(chatId)
                    }
                }
                chatMessageDAO.updateStatusBefore(fetchedAt, ChatMessageStatus.SENDING, ChatMessageStatus.ERROR)
                chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofFetched())
            }
            return@withContext response.toEmptyResponse()
        }
    }

    override suspend fun clearChatMessages() {
        withContext(ioDispatcher) {
            chatMessageDAO.updateStatus(ChatMessageStatus.SENDING, ChatMessageStatus.ERROR)
            chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofFetched())
        }
    }

    override suspend fun clearChatMessages(chatMessageIds: List<UUID>) {
        withContext(ioDispatcher) {
            chatMessageDAO.updateStatusByIds(chatMessageIds, ChatMessageStatus.ERROR)
            chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofFetched())
        }
    }

    override suspend fun clearChatMessage(chatMessageId: UUID?) {
        withContext(ioDispatcher) {
            chatMessageDAO.updateStatusById(chatMessageId, ChatMessageStatus.ERROR)
            chatMessageInvalidationListener?.onInvalidate(ChatMessageInvalidation.ofFetched())
        }
    }

    private fun saveChatMessages(
        sentChatMessageDTOs: List<ChatMessageDTO>?,
        receivedChatMessageDTOs: List<ChatMessageDTO>?
    ): MutableSet<Long> {
        val chatIds = mutableSetOf<Long>()
        val sentChatMessageIds = mutableListOf<UUID>()
        val receivedChatMessageIds = mutableListOf<UUID>()
        val newChatMessages = mutableListOf<ChatMessage>()

        receivedChatMessageDTOs?.forEach { chatMessageDTO ->
            if (!chatMessageDAO.existsById(chatMessageDTO.id)) {
                chatMessageMapper.toReceivedChatMessage(chatMessageDTO)?.let { chatMessage ->
                    newChatMessages.add(chatMessage)
                    chatIds.add(chatMessage.chatId)
                }
            }
            chatMessageDTO.id?.let { id -> receivedChatMessageIds.add(id) }
        }

        sentChatMessageDTOs?.forEach { chatMessageDTO ->
            safeLet(chatMessageDTO.createdAt, chatMessageDAO.findById(chatMessageDTO.id)) { createdAt, chatMessage ->
                chatMessage.createdAt = createdAt
                chatMessage.status = ChatMessageStatus.SENT
                newChatMessages.add(chatMessage)
                chatIds.add(chatMessage.chatId)
            }
            chatMessageDTO.id?.let { id -> sentChatMessageIds.add(id) }
        }
        syncChatMessages(sentChatMessageIds, receivedChatMessageIds)
        chatMessageDAO.insert(newChatMessages)
        return chatIds
    }

    private fun syncChatMessages(
        sentChatMessageIds: List<UUID>,
        receivedChatMessageIds: List<UUID>
    ) {
        if (sentChatMessageIds.isEmpty() && receivedChatMessageIds.isEmpty()) return
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { _, _ -> }) {
            chatRDS.syncChatMessages(sentChatMessageIds, receivedChatMessageIds)
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
        CoroutineScope(ioDispatcher).launch {
            val chatMessages = arrayListOf<ChatMessage>()
            chatMessages.add(ChatMessage(12, "sent-1", ChatMessageStatus.SENDING, UUID.fromString("938249ad-0ffc-46cf-bd7d-dc4b28f1726b")))
            chatMessages.add(ChatMessage(12, "sent-2", ChatMessageStatus.SENDING, UUID.fromString("cc00800c-e74f-4dac-bd9b-f0ef487e7d9f")))
            chatMessages.add(ChatMessage(12, "sent-3", ChatMessageStatus.SENDING, UUID.fromString("05df0340-e581-4f23-a490-bcd8ced00553")))
            chatMessageDAO.insert(chatMessages)
        }


//        CoroutineScope(ioDispatcher).launch {
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
//                        chatMessages.add(ChatMessage(12, date.toString(), ChatMessageStatus.SENT, UUID.randomUUID(), date))
//                    } else {
//                        chatMessages.add(ChatMessage(12, date.toString(), ChatMessageStatus.RECEIVED, UUID.randomUUID(), date))
//                    }
//                }
//            }
//
//            for (i in 0..10) {
//                chatMessages.add(ChatMessage(12, "sending-$i", ChatMessageStatus.SENDING, UUID.randomUUID()))
//            }
//
//
//            chatMessageDAO.insert(chatMessages)
//        }


    }
}


//TODO: move logic from match to chat
//TODO: change logic savechatmessage and savematches and order
//TODO: receive and sent chatmessage should update the recent chatmessage
//TODO: check runinTransaction in chat, match, click repositories