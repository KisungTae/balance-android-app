package com.beeswork.balance.internal.exception

class ChatMessageNotFoundException: BaseException(CODE, null) {

    companion object {
        const val CODE = "chat_message_not_found_exception"
    }
}