package com.beeswork.balance.internal.exception

class AccessTokenNotFoundException : BaseException(CODE, null) {
    companion object {
        const val CODE = "access_token_not_found_exception"
    }
}