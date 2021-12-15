package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.chat.SyncChatMessagesBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ListChatMessagesDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider

import java.util.*

class ChatRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), ChatRDS {
    override suspend fun syncChatMessages(sentChatMessageIds: List<UUID>, receivedChatMessageIds: List<UUID>) {
        balanceAPI.syncChatMessages(SyncChatMessagesBody(sentChatMessageIds, receivedChatMessageIds))
    }

    override suspend fun listChatMessages(): Resource<ListChatMessagesDTO> {
        return getResult { balanceAPI.listChatMessages() }
    }
}