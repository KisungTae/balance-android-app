package com.beeswork.balance.domain.uistate.chat

import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.internal.constant.ExceptionCode

class UnmatchUIState(
    val unmatched: Boolean,
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
) : UIState(showLoading, showError, shouldLogout, exception) {

    companion object {

        fun ofSuccess(): UnmatchUIState {
            return UnmatchUIState(unmatched = true, showLoading = false, showError = false, shouldLogout = false, exception = null)
        }

        fun ofLoading(): UnmatchUIState {
            return UnmatchUIState(unmatched = false, showLoading = true, showError = false, shouldLogout = false, exception = null)
        }

        fun ofError(exception: Throwable?): UnmatchUIState {
            return UnmatchUIState(
                unmatched = false,
                showLoading = false,
                showError = true,
                shouldLogout = ExceptionCode.isLoginException(exception),
                exception = exception
            )
        }
    }
}