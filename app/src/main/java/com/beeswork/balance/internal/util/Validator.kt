package com.beeswork.balance.internal.util

import com.beeswork.balance.internal.constant.ExceptionCode

class Validator {

    companion object {
        fun validateLogin(error: String?): Boolean {
            return when (error) {
                null -> true
                ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,
                ExceptionCode.ACCOUNT_DELETED_EXCEPTION,
                ExceptionCode.ACCOUNT_BLOCKED_EXCEPTION,
                ExceptionCode.EXPIRED_JWT_EXCEPTION,
                ExceptionCode.INVALID_REFRESH_TOKEN_EXCEPTION,
                ExceptionCode.INVALID_JWT_TOKEN_EXCEPTION,
                ExceptionCode.ACCESS_TOKEN_NOT_FOUND_EXCEPTION,
                ExceptionCode.REFRESH_TOKEN_NOT_FOUND_EXCEPTION -> false
                else -> true
            }
        }
    }
}