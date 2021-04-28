package com.beeswork.balance.data.database.response

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO

class ChatMessagePagingRefresh(
    val type: Type,
    val chatId: Long? = null,
    val newChatMessage: NewChatMessage? = null
) {
    enum class Type {
        SEND,
        SENT,
        DELETED,
        RECEIVED,
        FETCHED
    }
}