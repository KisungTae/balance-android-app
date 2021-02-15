package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.ui.chat.ChatMessageDomain

interface ChatRepository {
    suspend fun fetchChatMessages(
        chatId: Long,
        recipientId: String,
        pageSize: Int
    ): Resource<List<ChatMessageDomain>>
}