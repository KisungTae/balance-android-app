package com.beeswork.balance.domain.uistate.photo

import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.internal.constant.ExceptionCode

class SyncPhotosUIState(
    val synced: Boolean,
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
) : UIState(showLoading, showError, shouldLogout, exception) {

    companion object {

        fun ofSuccess(): SyncPhotosUIState {
            return SyncPhotosUIState(synced = true, showLoading = false, showError = false, shouldLogout = false, exception = null)
        }

        fun ofLoading(): SyncPhotosUIState {
            return SyncPhotosUIState(synced = false, showLoading = true, showError = false, shouldLogout = false, exception = null)
        }

        fun ofError(exception: Throwable?): SyncPhotosUIState {
            return SyncPhotosUIState(
                synced = false,
                showLoading = false,
                showError = true,
                shouldLogout = ExceptionCode.isLoginException(exception),
                exception = exception
            )
        }
    }
}