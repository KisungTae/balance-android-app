package com.beeswork.balance.internal.exception

class PhotoNotExistException : BaseException(CODE, null) {
    companion object {
        const val CODE = "photo_not_exist_exception"
    }
}