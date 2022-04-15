package com.beeswork.balance.domain.uistate.photo

import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.internal.constant.ExceptionCode

class FetchPhotosUIState(
    val fetched: Boolean,
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
) : UIState(showLoading, showError, shouldLogout, exception) {

    companion object {

        fun ofSuccess(): FetchPhotosUIState {
            return FetchPhotosUIState(fetched = true, showLoading = false, showError = false, shouldLogout = false, exception = null)
        }

        fun ofLoading(): FetchPhotosUIState {
            return FetchPhotosUIState(fetched = false, showLoading = true, showError = false, shouldLogout = false, exception = null)
        }

        fun ofError(exception: Throwable?): FetchPhotosUIState {
            return FetchPhotosUIState(
                fetched = false,
                showLoading = false,
                showError = true,
                shouldLogout = ExceptionCode.isLoginException(exception),
                exception = exception
            )
        }
    }
}