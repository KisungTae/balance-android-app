package com.beeswork.balance.internal.exception

class NameMaxSizeExceedException: BaseException(CODE, null) {

    companion object {
        private const val CODE = "name_max_size_exceed_exception"
    }
}