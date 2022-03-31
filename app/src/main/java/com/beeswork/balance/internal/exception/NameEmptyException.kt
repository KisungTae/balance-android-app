package com.beeswork.balance.internal.exception

class NameEmptyException : BaseException(CODE, null) {
    companion object {
        private const val CODE = "name_empty_exception"
    }
}