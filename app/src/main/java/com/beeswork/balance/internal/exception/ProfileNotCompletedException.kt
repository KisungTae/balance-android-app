package com.beeswork.balance.internal.exception

class ProfileNotCompletedException: BaseException(CODE, null) {
    companion object {
        private const val CODE = "profile_not_completed_exception"
    }
}