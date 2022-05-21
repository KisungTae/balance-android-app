package com.beeswork.balance.domain.uistate.profile

import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.internal.constant.ExceptionCode

class SaveBioUIState(
    val saved: Boolean,
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
) : UIState(showLoading, showError, shouldLogout, exception) {

    companion object {
        fun ofSuccess(): SaveBioUIState {
            return SaveBioUIState(
                saved = true,
                showLoading = false,
                showError = false,
                shouldLogout = false,
                exception = null
            )
        }

        fun ofLoading(): SaveBioUIState {
            return SaveBioUIState(
                saved = false,
                showLoading = true,
                showError = false,
                shouldLogout = false,
                exception = null
            )
        }

        fun ofError(exception: Throwable?): SaveBioUIState {
            return SaveBioUIState(
                saved = false,
                showLoading = false,
                showError = true,
                shouldLogout = ExceptionCode.isLoginException(exception),
                exception = exception
            )
        }
    }
}