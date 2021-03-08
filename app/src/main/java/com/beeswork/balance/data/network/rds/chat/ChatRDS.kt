package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.ChatMessageResponse
import com.beeswork.balance.data.network.response.Resource
import java.util.*

interface ChatRDS {

    suspend fun syncChatMessages(
        accountId: UUID,
        identityToken: UUID,
        sentChatMessageIds: List<Long>,
        receivedChatMessageIds: List<Long>
    )

    suspend fun fetchChatMessages(
        accountId: String,
        identityToken: String,
        chatId: Long,
        recipientId: String,
        lastChatMessageId: Long
    ): Resource<List<ChatMessageResponse>>
}