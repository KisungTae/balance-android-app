package com.beeswork.balance.internal.exception

class BirthDateNotSavedException: BaseException(CODE, null) {

    companion object {
        private const val CODE = "birth_date_not_saved_exception"
    }
}