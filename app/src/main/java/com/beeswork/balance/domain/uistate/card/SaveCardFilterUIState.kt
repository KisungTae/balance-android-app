package com.beeswork.balance.domain.uistate.card

import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.internal.constant.ExceptionCode

class SaveCardFilterUIState(
    val saved: Boolean,
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
): UIState(showLoading, showError, shouldLogout, exception) {

    companion object {
        fun ofSuccess(): SaveCardFilterUIState {
            return SaveCardFilterUIState(saved = true, showLoading = false, showError = false, shouldLogout = false, exception = null)
        }

        fun ofError(exception: Throwable?): SaveCardFilterUIState {
            return SaveCardFilterUIState(
                saved = false,
                showLoading = false,
                showError = true,
                shouldLogout = ExceptionCode.isLoginException(exception),
                exception = exception
            )
        }
    }
}