package com.beeswork.balance.internal.constant

enum class ChatMessageStatus {
    SENDING,
    SENT,
    RECEIVED,
    ERROR,
    SEPARATOR,;

    fun isProcessed(): Boolean {
        return (this == RECEIVED || this == SENT)
    }
}


