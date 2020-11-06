package com.beeswork.balance.internal.constant

class ExceptionCode {

    companion object {
        const val EXCEPTION = "exception"

        const val NO_INTERNET_CONNECTIVITY_EXCEPTION = "no.internet.connectivity.exception"
        const val SOCKET_TIMEOUT_EXCEPTION = "socket.timeout.exception"

        const val BAD_REQUEST_EXCEPTION = "bad.request.exception"

        const val ACCOUNT_NOT_FOUND_EXCEPTION = "account.not.found.exception"
        const val ACCOUNT_SHORT_OF_POINT_EXCEPTION = "account.short.of.point.exception"
        const val ACCOUNT_BLOCKED_EXCEPTION = "account.blocked.exception"

        const val QUESTION_NOT_FOUND_EXCEPTION = "question.not.found.exception"
        const val QUESTION_SET_CHANGED_EXCEPTION = "question.set.changed.exception"


        const val SWIPE_CLICKED_EXISTS_EXCEPTION = "swipe.clicked.exists.exception"
        const val SWIPE_NOT_FOUND_EXCEPTION = "swipe.not.found.exception"
        const val SWIPED_NOT_FOUND_EXCEPTION = "swiped.not.found.exception"
        const val SWIPED_BLOCKED_EXCEPTION = "swiped.blocked.exception"

    }

}