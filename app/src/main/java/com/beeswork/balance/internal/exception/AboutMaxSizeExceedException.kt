package com.beeswork.balance.internal.exception

class AboutMaxSizeExceedException: BaseException(CODE, null) {

    companion object {
        const val CODE = "about_max_size_exceed_exception"
    }
}