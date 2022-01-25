package com.beeswork.balance.internal.exception

class AccountIdNotFoundException: BaseException(CODE, null) {
    companion object {
        const val CODE = "account_id_not_found_exception"
    }
}