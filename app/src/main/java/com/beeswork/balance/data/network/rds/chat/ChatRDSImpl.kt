package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.chat.SyncChatMessagesBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO

import java.util.*

class ChatRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ChatRDS {

    override suspend fun fetchChatMessages(chatId: UUID, lastChatMessageId: Long?, loadSize: Int): Resource<List<ChatMessageDTO>> {
        return getResult { balanceAPI.fetchChatMessages(chatId, lastChatMessageId, loadSize) }
    }

    override suspend fun listChatMessages(chatId: UUID, appToken: UUID, startPosition: Int, loadSize: Int): Resource<List<ChatMessageDTO>> {
        return getResult { balanceAPI.listChatMessages(chatId, appToken, startPosition, loadSize) }
    }

    override suspend fun syncChatMessages(chatId: UUID, appToken: UUID, chatMessageIds: List<Long>) {
        balanceAPI.syncChatMessages(SyncChatMessagesBody(chatId, appToken, chatMessageIds))
    }
}