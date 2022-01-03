package com.beeswork.balance.data.database.repository.chat

class ChatMessageInvalidation(
    val type: Type,
    val chatId: Long?,
    val body: String?
) {

    companion object {
        fun ofReceipt(chatId: Long?): ChatMessageInvalidation {
            return ChatMessageInvalidation(Type.RECEIPT, chatId, null)
        }

        fun ofSend(chatId: Long?): ChatMessageInvalidation {
            return ChatMessageInvalidation(Type.SEND, chatId, null)
        }

        fun ofDelete(chatId: Long?): ChatMessageInvalidation {
            return ChatMessageInvalidation(Type.DELETED, chatId, null)
        }

        fun ofReceived(chatId: Long?, body: String): ChatMessageInvalidation {
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