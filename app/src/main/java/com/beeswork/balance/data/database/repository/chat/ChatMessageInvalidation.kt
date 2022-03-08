package com.beeswork.balance.data.database.repository.chat

import java.util.*

class ChatMessageInvalidation(
    val type: Type,
    val chatId: UUID?,
    val body: String?
) {

    companion object {
        fun ofReceipt(chatId: UUID?): ChatMessageInvalidation {
            return ChatMessageInvalidation(Type.RECEIPT, chatId, null)
        }

        fun ofSend(chatId: UUID?): ChatMessageInvalidation {
            return ChatMessageInvalidation(Type.SEND, chatId, null)
        }

        fun ofDelete(chatId: UUID?): ChatMessageInvalidation {
            return ChatMessageInvalidation(Type.DELETED, chatId, null)
        }

        fun ofReceived(chatId: UUID?, body: String): ChatMessageInvalidation {
            return ChatMessageInvalidation(Type.RECEIVED, chatId, body)
        }

        fun ofFetched(): ChatMessageInvalidation {
            return ChatMessageInvalidation(Type.FETCHED, null, null)
        }
    }

    enum class Type {
        SEND,
        SENT,
        DELETED,
        RECEIVED,
        FETCHED,
        RECEIPT
    }
}