package com.beeswork.balance.domain.uistate.register

import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.internal.constant.ExceptionCode

class SaveProfileUIState(
    val saved: Boolean,
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
) : UIState(showLoading, showError, shouldLogout, exception) {


    companion object {

        fun ofSuccess(): SaveProfileUIState {
            return SaveProfileUIState(saved = true, showLoading = false, showError = false, shouldLogout = false, exception = null)
        }

        fun ofLoading(): SaveProfileUIState {
            return SaveProfileUIState(saved = false, showLoading = true, showError = false, shouldLogout = false, exception = null)
        }

        fun ofError(exception: Throwable?): SaveProfileUIState {
            return SaveProfileUIState(
                saved = false,
                showLoading = false,
                showError = true,
                shouldLogout = ExceptionCode.isLoginException(exception),
                exception = exception
            )
        }
    }
}