package com.beeswork.balance.internal.exception

class ProfileNotFoundException: BaseException(CODE, null) {

    companion object {
        private const val CODE = "profile_not_found_exception"
    }
}