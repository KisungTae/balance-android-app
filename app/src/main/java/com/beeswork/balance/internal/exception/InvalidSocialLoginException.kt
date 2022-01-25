package com.beeswork.balance.internal.exception

class InvalidSocialLoginException: BaseException(CODE, null) {

    companion object {
        const val CODE = "invalid_social_login_exception"
    }
}