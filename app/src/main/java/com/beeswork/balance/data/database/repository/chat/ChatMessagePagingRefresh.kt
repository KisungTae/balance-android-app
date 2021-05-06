package com.beeswork.balance.data.database.repository.chat

class ChatMessagePagingRefresh(
    val type: Type,
    val chatId: Long? = null,
    val body: String? = null
) {
    enum class Type {
        SEND,
        SENT,
        DELETED,
        RECEIVED,
        FETCHED
    }
}