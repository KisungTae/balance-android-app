package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.chat.ChatMessageReceiptDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ChatMessageStatus
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ChatRepository {
    val chatMessageInvalidationFlow: Flow<ChatMessageInvalidation>
    val chatMessageReceiptFlow: Flow<Resource<EmptyResponse>>
    val sendChatMessageFlow: Flow<ChatMessageDTO>

    suspend fun deleteChatMessages()
    suspend fun sendChatMessage(chatId: Long, swipedId: UUID, body: String)
    suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage>
    suspend fun resendChatMessage(key: Long?)
    suspend fun deleteChatMessage(chatId: Long, key: Long)
    suspend fun saveChatMessageReceived(chatMessageDTO: ChatMessageDTO)
    suspend fun saveChatMessageReceipt(chatMessageReceiptDTO: ChatMessageReceiptDTO)
    suspend fun fetchChatMessages(): Resource<EmptyResponse>
    suspend fun connectStomp()
    suspend fun updateChatMessageStatus(chatMessageKeys: List<Long>, chatMessageStatus: ChatMessageStatus)

    fun test()
}