package com.beeswork.balance.internal.exception

class PhotoNotSupportedTypeException: BaseException(CODE, null) {
    companion object {
        const val CODE = "photo_not_supported_type_exception"
    }
}