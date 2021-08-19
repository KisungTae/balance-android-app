package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ListChatMessagesDTO

import java.util.*

interface ChatRDS {
    suspend fun syncChatMessages(sentChatMessageIds: List<Long>, receivedChatMessageIds: List<Long>)
    suspend fun listChatMessages(accountId: UUID): Resource<ListChatMessagesDTO>
}