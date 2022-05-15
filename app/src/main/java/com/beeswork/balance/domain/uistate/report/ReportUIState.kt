package com.beeswork.balance.domain.uistate.report

import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.internal.constant.ExceptionCode

class ReportUIState(
    val reported: Boolean,
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
) : UIState(showLoading, showError, shouldLogout, exception) {

    companion object {
        fun ofSuccess(): ReportUIState {
            return ReportUIState(reported = true, showLoading = false, showError = false, shouldLogout = false, exception = null)
        }

        fun ofLoading(): ReportUIState {
            return ReportUIState(reported = false, showLoading = true, showError = false, shouldLogout = false, exception = null)
        }

        fun ofError(exception: Throwable?): ReportUIState {
            return ReportUIState(
                reported = false,
                showLoading = false,
                showError = true,
                shouldLogout = ExceptionCode.isLoginException(exception),
                exception = exception
            )
        }
    }
}