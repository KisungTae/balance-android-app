package com.beeswork.balance.data.database.repository.chat

class ChatMessageInvalidation(
    val type: Type,
    val chatId: Long? = null,
    val body: String? = null
) {
    enum class Type {
        SEND,
        SENT,
        DELETED,
        RECEIVED,
        FETCHED,
        RECEIPT
    }
}