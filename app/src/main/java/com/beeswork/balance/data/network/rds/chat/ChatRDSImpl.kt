package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.SyncChatMessagesBody
import com.beeswork.balance.data.network.response.ChatMessageResponse
import com.beeswork.balance.data.network.response.Resource
import java.util.*

class ChatRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ChatRDS {
    override suspend fun syncChatMessages(
        accountId: UUID,
        identityToken: UUID,
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

    override suspend fun fetchChatMessages(
        accountId: String,
        identityToken: String,
        chatId: Long,
        recipientId: String,
        lastChatMessageId: Long
    ): Resource<List<ChatMessageResponse>> {
        return getResult {
            balanceAPI.fetchChatMessages(
                accountId,
                identityToken,
                chatId,
                recipientId,
                lastChatMessageId
            )
        }
    }
}