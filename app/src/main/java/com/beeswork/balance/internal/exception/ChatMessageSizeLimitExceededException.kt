package com.beeswork.balance.internal.exception

class ChatMessageSizeLimitExceededException: BaseException(CODE, null) {
    companion object {
        const val CODE = "chat_message_size_limit_exceeded_exception"
    }
}