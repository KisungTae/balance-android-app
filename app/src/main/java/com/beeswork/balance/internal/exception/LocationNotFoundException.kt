package com.beeswork.balance.internal.exception

class LocationNotFoundException: BaseException(CODE, null) {

    companion object {
        const val CODE = "location_not_found_exception"
    }
}