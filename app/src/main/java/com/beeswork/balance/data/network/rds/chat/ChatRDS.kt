package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.ChatMessageResponse
import com.beeswork.balance.data.network.response.Resource
import java.util.*

interface ChatRDS {

    suspend fun receivedChatMessages(
        accountId: UUID,
        identityToken: UUID,
        chatMessageIds: List<Long>
    )

    suspend fun fetchChatMessages(
        accountId: String,
        identityToken: String,
        chatId: Long,
        recipientId: String,
        lastChatMessageId: Long
    ): Resource<List<ChatMessageResponse>>
}