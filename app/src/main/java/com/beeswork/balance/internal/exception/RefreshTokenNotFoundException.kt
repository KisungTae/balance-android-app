package com.beeswork.balance.internal.exception

class RefreshTokenNotFoundException: BaseException(CODE, null) {
    companion object {
        const val CODE = "refresh_token_not_found_exception"
    }
}