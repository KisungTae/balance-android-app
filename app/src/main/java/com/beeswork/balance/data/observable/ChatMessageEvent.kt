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
        fun fetchError(error: String?, errorMessage: String?): ChatMessageEvent {
            return ChatMessageEvent(null, null, error, errorMessage, Type.FETCH_ERROR)
        }

        fun fetch(chatMessages: List<ChatMessage>): ChatMessageEvent {
            return ChatMessageEvent(chatMessages, null, null, null, Type.FETCH)
        }
    }

    enum class Type {
        FETCH_ERROR,
        ERROR,
        FETCH,
        APPEND,
        PREPEND,
        SENT,
        SYNCED,
        END
    }
}