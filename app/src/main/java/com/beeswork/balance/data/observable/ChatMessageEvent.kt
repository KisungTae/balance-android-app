package com.beeswork.balance.data.observable

import com.beeswork.balance.data.database.entity.ChatMessage

class ChatMessageEvent(
    val chatMessages: List<ChatMessage>?,
    val chatMessage: ChatMessage?,
    val error: String?,
    val errorMessage: String?,
    val type: Type
) {

    companion object {
        fun error(error: String?, errorMessage: String?): ChatMessageEvent {
            return ChatMessageEvent(null, null, error, errorMessage, Type.ERROR)
        }
    }

    enum class Type {
        ERROR,
        FETCH,
        APPEND,
        PREPEND,
        SENT,
        SYNCED
    }
}