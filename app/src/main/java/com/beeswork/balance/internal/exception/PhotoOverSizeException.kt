package com.beeswork.balance.internal.exception

class PhotoOverSizeException : BaseException(CODE, null) {
    companion object {
        const val CODE = "photo_over_size_exception"
    }
}