package com.beeswork.balance.ui.chat

import com.beeswork.balance.data.database.entity.ChatMessage

class ChatMessageEvent(
    val chatMessages: List<ChatMessage>?,
    val chatMessage: ChatMessage?,
    val error: String?,
    val errorMessage: String?,
    val type: Type
) {

    companion object {
        fun fetchInitialError(error: String?, errorMessage: String?): ChatMessageEvent {
            return ChatMessageEvent(null, null, error, errorMessage, Type.FETCH_INITIAL_ERROR)
        }

        fun fetchInitial(chatMessages: List<ChatMessage>): ChatMessageEvent {
            return ChatMessageEvent(chatMessages, null, null, null, Type.FETCH_INITIAL)
        }
    }

    enum class Type {
        FETCH_INITIAL_ERROR,
        ERROR,
        FETCH_INITIAL,
        APPEND,
        PREPEND,
        SENT,
        SYNCED,
        END
    }
}