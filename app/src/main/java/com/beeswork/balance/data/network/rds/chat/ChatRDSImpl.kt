package com.beeswork.balance.data.network.rds.chat

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.chat.FetchedChatMessageBody
import com.beeswork.balance.data.network.request.chat.ReceivedChatMessageBody
import com.beeswork.balance.data.network.request.chat.SyncChatMessagesBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.chat.ListChatMessagesDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse

import java.util.*

class ChatRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ChatRDS {

    override suspend fun fetchChatMessages(loadSize: Int, chatId: UUID, lastChatMessageId: Long?): Resource<List<ChatMessageDTO>> {
        return getResult { balanceAPI.fetchChatMessages(chatId, lastChatMessageId, loadSize) }
    }

    override suspend fun syncChatMessages(sentChatMessageIds: List<UUID>, receivedChatMessageIds: List<UUID>) {
        balanceAPI.syncChatMessages(SyncChatMessagesBody(sentChatMessageIds, receivedChatMessageIds))
    }

    override suspend fun listChatMessages(): Resource<ListChatMessagesDTO> {
        return getResult { balanceAPI.listChatMessages() }
    }

    override suspend fun fetchedChatMessage(chatMessageId: UUID): Resource<EmptyResponse> {
        return getResult { balanceAPI.fetchedChatMessage(FetchedChatMessageBody(chatMessageId)) }
    }

    override suspend fun receivedChatMessage(chatMessageId: UUID): Resource<EmptyResponse> {
        return getResult { balanceAPI.receivedChatMessage(ReceivedChatMessageBody(chatMessageId)) }
    }
}