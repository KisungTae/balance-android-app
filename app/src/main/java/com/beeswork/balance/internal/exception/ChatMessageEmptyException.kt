package com.beeswork.balance.internal.exception

class ChatMessageEmptyException : BaseException(CODE, null) {
    companion object {
        const val CODE = "chat_message_empty_exception"
    }
}