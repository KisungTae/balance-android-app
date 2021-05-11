package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.SyncChatMessagesBody
import com.beeswork.balance.data.network.response.ChatMessageResponse
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ListChatMessagesDTO
import java.util.*

class ChatRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ChatRDS {
    override suspend fun syncChatMessages(
        accountId: UUID?,
        identityToken: UUID?,
        sentChatMessageIds: List<Long>,
        receivedChatMessageIds: List<Long>
    ) {
        balanceAPI.syncChatMessages(
            SyncChatMessagesBody(
                accountId,
                identityToken,
                sentChatMessageIds,
                receivedChatMessageIds
            )
        )
    }
}