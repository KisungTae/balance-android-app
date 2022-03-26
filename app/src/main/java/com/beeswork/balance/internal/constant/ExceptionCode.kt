package com.beeswork.balance.internal.constant

import com.beeswork.balance.internal.exception.*

class ExceptionCode {

    companion object {

        const val EXPIRED_JWT_EXCEPTION = "expired.jwt.exception"
        const val INVALID_JWT_TOKEN_EXCEPTION = "invalid.jwt.token.exception"
        const val ACCOUNT_NOT_FOUND_EXCEPTION = "account.not.found.exception"
        const val ACCOUNT_BLOCKED_EXCEPTION = "account.blocked.exception"
        const val ACCOUNT_DELETED_EXCEPTION = "account.deleted.exception"

        const val PHOTO_ALREADY_EXIST_EXCEPTION = "photo.already.exists.exception"
        const val MATCH_UNMATCHED_EXCEPTION = "match.unmatched.exception"
        const val INVALID_REFRESH_TOKEN_EXCEPTION = "invalid.refresh.token.exception"
        const val AUTHENTICATION_EXCEPTION = "authentication.exception"

        fun isExpiredJWTTokenException(exception: Throwable?): Boolean {
            return exception is BaseException && exception.error == EXPIRED_JWT_EXCEPTION
        }

        fun isLoginException(exception: Throwable?): Boolean {
            if (exception == null) {
                return false
            }
            if (exception is AccessTokenNotFoundException
                || exception is RefreshTokenNotFoundException
                || exception is AccountIdNotFoundException) {
                return true
            }
            if (exception is BaseException) {
                return isLoginException(exception.error)
            }
            return false
        }

        fun isLoginException(error: String?): Boolean {
            when (error) {
                null -> return false
                ACCOUNT_NOT_FOUND_EXCEPTION,
                ACCOUNT_DELETED_EXCEPTION,
                ACCOUNT_BLOCKED_EXCEPTION,
                EXPIRED_JWT_EXCEPTION,
                INVALID_REFRESH_TOKEN_EXCEPTION,
                INVALID_JWT_TOKEN_EXCEPTION,
                AUTHENTICATION_EXCEPTION -> return true
            }
            return false
        }


    }

}