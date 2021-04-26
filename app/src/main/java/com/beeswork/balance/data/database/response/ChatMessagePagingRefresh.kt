package com.beeswork.balance.data.database.response

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO

class ChatMessagePagingRefresh(
    val newChatMessage: NewChatMessage?,
    val type: Type
) {
    enum class Type {
        SEND,
        RECEIVED,
        FETCHED
    }
}