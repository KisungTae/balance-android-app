package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime
import java.util.*

interface ChatRepository {
    val chatMessageInvalidationFlow: Flow<ChatMessageInvalidation>
    val chatMessageReceiptFlow: Flow<Resource<EmptyResponse>>

    suspend fun deleteChatMessages()
    suspend fun sendChatMessage(chatId: Long, swipedId: UUID, body: String)
    suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage>
    suspend fun resendChatMessage(key: Long, swipedId: UUID)
    suspend fun deleteChatMessage(chatId: Long, key: Long)
    suspend fun saveChatMessageReceived(chatMessageDTO: ChatMessageDTO)
    suspend fun fetchChatMessages(): Resource<EmptyResponse>
    suspend fun connectStomp()

    fun test()
}