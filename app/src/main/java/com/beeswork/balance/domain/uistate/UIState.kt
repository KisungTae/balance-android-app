package com.beeswork.balance.domain.uistate

import com.beeswork.balance.internal.constant.ExceptionCode

open class UIState(
    val showLoading: Boolean,
    val showError: Boolean,
    val shouldLogout: Boolean,
    val exception: Throwable?
) {
    companion object {

        fun ofLoading(): UIState {
            return UIState(
                showLoading = true,
                showError = false,
                shouldLogout = false,
                exception = null
            )
        }

        fun ofError(exception: Throwable?): UIState {
            return UIState(
                showLoading = false,
                showError = true,
                shouldLogout = ExceptionCode.isLoginException(exception),
                exception = exception
            )
        }
    }
}