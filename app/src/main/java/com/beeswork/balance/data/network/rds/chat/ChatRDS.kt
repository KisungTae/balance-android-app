package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ListChatMessagesDTO
import java.util.*

interface ChatRDS {
    suspend fun syncChatMessages(sentChatMessageIds: List<UUID>, receivedChatMessageIds: List<UUID>)
    suspend fun listChatMessages(): Resource<ListChatMessagesDTO>
}