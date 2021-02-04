package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.chat.ChatMessageDomain

class ChatRepositoryImpl(
    private val chatRDS: ChatRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val preferenceProvider: PreferenceProvider
): ChatRepository {
    override suspend fun fetchChatMessages(
        chatId: Long,
        recipientId: String,
        pageSize: Int
    ): Resource<List<ChatMessageDomain>> {
        // TODO: change lastReadAt to
        val
    }

}