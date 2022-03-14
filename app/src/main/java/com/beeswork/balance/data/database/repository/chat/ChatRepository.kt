package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.chat.ChatMessageReceiptDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ChatRepository {

    val chatPageInvalidationFlow: Flow<ChatMessage?>
    val sendChatMessageFlow: Flow<ChatMessageDTO>

    suspend fun sendChatMessage(chatId: UUID, body: String): Resource<EmptyResponse>
    suspend fun resendChatMessage(tag: UUID): Resource<EmptyResponse>
    suspend fun fetchChatMessages(chatId: UUID, lastChatMessageId: Long?, loadSize: Int): Resource<List<ChatMessageDTO>>
    suspend fun loadChatMessages(chatId: UUID, startPosition: Int, loadSize: Int): List<ChatMessage>




    suspend fun deleteChatMessages()
    suspend fun deleteChatMessage(chatId: Long, key: Long)
    suspend fun saveChatMessageReceived(chatMessageDTO: ChatMessageDTO)
    suspend fun saveChatMessageReceipt(chatMessageReceiptDTO: ChatMessageReceiptDTO)
    suspend fun clearChatMessages()
    suspend fun clearChatMessage(chatMessageId: UUID?)
    suspend fun clearChatMessages(chatMessageIds: List<UUID>)


    fun test()
}