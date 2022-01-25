package com.beeswork.balance.internal.exception

class NoInternetConnectivityException : BaseException(CODE, null) {
    companion object {
        const val CODE = "no_internet_connectivity_exception"
    }
}