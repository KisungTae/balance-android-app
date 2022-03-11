package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.chat.ListChatMessagesDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import java.util.*

interface ChatRDS {
    suspend fun fetchChatMessages(loadSize: Int, chatId: UUID, lastChatMessageId: Long?): Resource<List<ChatMessageDTO>>
    suspend fun syncChatMessages(sentChatMessageIds: List<UUID>, receivedChatMessageIds: List<UUID>)
    suspend fun listChatMessages(): Resource<ListChatMessagesDTO>
    suspend fun fetchedChatMessage(chatMessageId: UUID): Resource<EmptyResponse>
    suspend fun receivedChatMessage(chatMessageId: UUID): Resource<EmptyResponse>
}