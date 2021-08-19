package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.SyncChatMessagesBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ListChatMessagesDTO

import java.util.*

class ChatRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ChatRDS {
    override suspend fun syncChatMessages(sentChatMessageIds: List<Long>, receivedChatMessageIds: List<Long>) {
        balanceAPI.syncChatMessages(SyncChatMessagesBody(sentChatMessageIds, receivedChatMessageIds))
    }

    override suspend fun listChatMessages(accountId: UUID): Resource<ListChatMessagesDTO> {
        return getResult { balanceAPI.listChatMessages(accountId) }
    }
}