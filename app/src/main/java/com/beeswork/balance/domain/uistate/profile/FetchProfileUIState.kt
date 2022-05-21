package com.beeswork.balance.domain.uistate.profile

import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.internal.constant.ExceptionCode

class FetchProfileUIState(
    val profileUIState: ProfileUIState?,
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
) : UIState(showLoading, showError, shouldLogout, exception) {


    companion object {
        fun ofSuccess(profileUIState: ProfileUIState): FetchProfileUIState {
            return FetchProfileUIState(
                profileUIState,
                showLoading = false,
                showError = false,
                shouldLogout = false,
                exception = null
            )
        }

        fun ofLoading(): FetchProfileUIState {
            return FetchProfileUIState(
                profileUIState = null,
                showLoading = true,
                showError = false,
                shouldLogout = false,
                exception = null)
        }

        fun ofError(exception: Throwable?): FetchProfileUIState {
            return FetchProfileUIState(
                profileUIState = null,
                showLoading = false,
                showError = true,
                shouldLogout = ExceptionCode.isLoginException(exception),
                exception = exception
            )
        }
    }


}