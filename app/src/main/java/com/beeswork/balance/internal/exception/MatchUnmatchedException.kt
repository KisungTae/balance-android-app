package com.beeswork.balance.internal.exception

class MatchUnmatchedException: BaseException(CODE, null) {
    companion object {
        const val CODE = "match_unmatched_exception"
    }
}