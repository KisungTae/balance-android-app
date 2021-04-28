package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.response.ChatMessagePagingRefresh
import com.beeswork.balance.data.database.response.MatchPagingRefresh
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ChatRepository {
    val chatMessagePagingRefreshFlow: Flow<ChatMessagePagingRefresh>
    val sendChatMessageFlow: Flow<Resource<EmptyResponse>>

    suspend fun sendChatMessage(chatId: Long, matchedId: UUID, body: String)
    suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage>
    suspend fun resendChatMessage(key: Long, matchedId: UUID)
    suspend fun deleteChatMessage(chatId: Long, key: Long)

    fun test()
}