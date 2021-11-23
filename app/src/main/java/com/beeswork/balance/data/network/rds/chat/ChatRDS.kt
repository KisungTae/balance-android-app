package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ListChatMessagesDTO

interface ChatRDS {
    suspend fun syncChatMessages(sentChatMessageIds: List<Long>, receivedChatMessageIds: List<Long>)
    suspend fun listChatMessages(): Resource<ListChatMessagesDTO>
}