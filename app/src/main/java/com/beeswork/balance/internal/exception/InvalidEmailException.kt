package com.beeswork.balance.internal.exception

class InvalidEmailException : BaseException(CODE, null) {
    companion object {
        const val CODE = "invalid_email_exception"
    }
}