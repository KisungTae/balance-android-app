package com.beeswork.balance.internal.exception

class CardFilterNotFoundException: BaseException(CODE, null) {

    companion object {
        private const val CODE = "card_filter_not_found_exception"
    }
}