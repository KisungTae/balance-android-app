package com.beeswork.balance.internal.constant

import com.beeswork.balance.internal.exception.NoInternetConnectivityException

class ExceptionCode {

    companion object {
        const val EXCEPTION = "exception"

        const val REFRESH_TOKEN_NOT_FOUND_EXCEPTION = "refresh_token_not_found_exception"
        const val ACCESS_TOKEN_NOT_FOUND_EXCEPTION = "access_token_not_found_exception"
        const val EXPIRED_JWT_TOKEN_EXCEPTION = "expired.jwt.token.exception"
        const val INVALID_JWT_TOKEN_EXCEPTION = "invalid.jwt.token.exception"

        const val NO_INTERNET_CONNECTIVITY_EXCEPTION = "no_internet_connectivity_exception"
        const val SOCKET_TIMEOUT_EXCEPTION = "socket_timeout_exception"
        const val CONNECT_EXCEPTION = "connect_exception"
        const val NETWORK_EXCEPTION = "network.exception"
        const val UNKNOWN_HOST_EXCEPTION = "unknown_host_exception"

        const val BAD_REQUEST_EXCEPTION = "bad_request_exception"

        const val ACCOUNT_NOT_FOUND_EXCEPTION = "account.not.found.exception"
        const val ACCOUNT_BLOCKED_EXCEPTION = "account.blocked.exception"
        const val ACCOUNT_DELETED_EXCEPTION = "account.deleted.exception"
        const val ACCOUNT_ID_NOT_FOUND_EXCEPTION = "account.id.not.found.exception"
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

        const val MATCH_UNMATCHED_EXCEPTION = "match.unmatched.exception"

        const val INVALID_EMAIL_EXCEPTION = "invalid_email_exception"

        const val INVALID_SOCIAL_LOGIN_EXCEPTION = "invalid_social_login_exception"

        const val ACCOUNT_IDENTITY_NOT_FOUND_EXCEPTION = "account_identity_not_found_exception"

        const val INVALID_REFRESH_TOKEN_EXCEPTION = "invalid.refresh.token.exception"
        const val REFRESH_TOKEN_KEY_NOT_FOUND_EXCEPTION = "refresh.token.key.not.found.exception"


        fun isLoginException(error: String?): Boolean {
            return when (error) {
                null -> false
                ACCOUNT_NOT_FOUND_EXCEPTION,
                ACCOUNT_DELETED_EXCEPTION,
                ACCOUNT_BLOCKED_EXCEPTION,
                EXPIRED_JWT_TOKEN_EXCEPTION,
                INVALID_REFRESH_TOKEN_EXCEPTION,
                INVALID_JWT_TOKEN_EXCEPTION,
                ACCESS_TOKEN_NOT_FOUND_EXCEPTION,
                REFRESH_TOKEN_NOT_FOUND_EXCEPTION,
                ACCOUNT_ID_NOT_FOUND_EXCEPTION -> true
                else -> false
            }
        }

        fun getExceptionCodeFrom(exception: Throwable): String? {
            return when (exception) {
                is NoInternetConnectivityException -> NO_INTERNET_CONNECTIVITY_EXCEPTION
                else -> null
            }
        }

    }

}