package com.beeswork.balance.internal.constant

class ExceptionCode {

    companion object {
        const val EXCEPTION = "exception"

        const val NO_INTERNET_CONNECTIVITY_EXCEPTION = "no_internet_connectivity_exception"
        const val SOCKET_TIMEOUT_EXCEPTION = "socket_timeout_exception"
        const val CONNECT_EXCEPTION = "connect_exception"
        const val NETWORK_EXCEPTION = "network.exception"
        const val UNKNOWN_HOST_EXCEPTION = "unknown_host_exception"

        const val BAD_REQUEST_EXCEPTION = "bad_request_exception"

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


        const val PHOTO_OVER_SIZE_EXCEPTION = "photo_over_size_exception"
        const val PHOTO_NOT_EXIST_EXCEPTION = "photo_not_exist_exception"
        const val PHOTO_NOT_FOUND_EXCEPTION = "photo.not.found.exception"
        const val PHOTO_NOT_SUPPORTED_TYPE_EXCEPTION = "photo_not_supported_type_exception"
        const val PHOTO_ALREADY_EXIST_EXCEPTION = "photo.already.exists.exception"
        const val PHOTO_NOT_ORDERABLE_EXCEPTION = "photo_not_orderable_exception"

        const val CHAT_MESSAGE_OVER_SIZED_EXCEPTION = "chat_message_over_sized_exception"
        const val CHAT_MESSAGE_EMPTY_EXCEPTION = "chat_message_empty_exception"

        const val MATCH_UNMATCHED_EXCEPTION = "match_unmatched_exception"

        const val INVALID_EMAIL_EXCEPTION = "invalid_email_exception"

        const val INVALID_SOCIAL_LOGIN_EXCEPTION = "invalid_social_login_exception"

    }

}