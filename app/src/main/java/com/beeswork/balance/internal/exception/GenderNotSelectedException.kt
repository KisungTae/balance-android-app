package com.beeswork.balance.internal.exception

class GenderNotSelectedException: BaseException(CODE, null) {

    companion object {
        private const val CODE = "gender_not_selected_exception"
    }
}