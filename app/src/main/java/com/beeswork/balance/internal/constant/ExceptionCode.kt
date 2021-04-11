package com.beeswork.balance.internal.constant

class ExceptionCode {

    companion object {
        const val EXCEPTION = "exception"

        const val NO_INTERNET_CONNECTIVITY_EXCEPTION = "no_internet_connectivity_exception"
        const val SOCKET_TIMEOUT_EXCEPTION = "socket_timeout_exception"
        const val CONNECT_EXCEPTION = "connect_exception"
        const val NETWORK_EXCEPTION = "network.exception"

        const val BAD_REQUEST_EXCEPTION = "bad.request.exception"

        const val ACCOUNT_NOT_FOUND_EXCEPTION = "account.not.found.exception"
        const val ACCOUNT_BLOCKED_EXCEPTION = "account.blocked.exception"
        const val ACCOUNT_DELETED_EXCEPTION = "account.deleted.exception"
        const val ACCOUNT_SHORT_OF_POINT_EXCEPTION = "account.short.of.point.exception"

        const val QUESTION_NOT_FOUND_EXCEPTION = "question.not.found.exception"
        const val QUESTION_SET_CHANGED_EXCEPTION = "question.set.changed.exception"


        const val SWIPE_CLICKED_EXISTS_EXCEPTION = "swipe.clicked.exists.exception"
        const val SWIPE_NOT_FOUND_EXCEPTION = "swipe.not.found.exception"
        const val SWIPED_NOT_FOUND_EXCEPTION = "swiped.not.found.exception"
        const val SWIPED_BLOCKED_EXCEPTION = "swiped.blocked.exception"


        const val PHOTO_OVER_SIZED_EXCEPTION = "photo_over_sized_exception"
        const val PHOTO_NOT_FOUND_EXCEPTION = "photo.not.found.exception"
        const val PHOTO_NOT_EXIST_EXCEPTION = "photo.not.exist.exception"

        const val CHAT_MESSAGE_OVER_SIZED_EXCEPTION = "chat_message_over_sized_exception"
        const val CHAT_MESSAGE_EMPTY_EXCEPTION = "chat_message_empty_exception"

    }

}