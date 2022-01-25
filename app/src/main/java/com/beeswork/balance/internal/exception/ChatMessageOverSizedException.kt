package com.beeswork.balance.internal.exception

class ChatMessageOverSizedException: BaseException(CODE, null) {
    companion object {
        const val CODE = "chat_message_over_sized_exception"
    }
}