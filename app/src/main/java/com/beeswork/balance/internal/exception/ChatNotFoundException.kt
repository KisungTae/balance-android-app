package com.beeswork.balance.internal.exception

class ChatNotFoundException: BaseException(CODE, null) {
    companion object {
        const val CODE = "chat_not_found_exception"
    }
}