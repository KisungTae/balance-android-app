package com.beeswork.balance.domain.uistate.balancegame

class SaveAnswersUIState(
    val saved: Boolean,
    val showLoading: Boolean,
    val showError: Boolean,
    val exception: Throwable?
) {

    companion object {

        fun ofSuccess(): SaveAnswersUIState {
            return SaveAnswersUIState(saved = true, showLoading = false, showError = false, exception = null)
        }

        fun ofLoading(): SaveAnswersUIState {
            return SaveAnswersUIState(saved = false, showLoading = true, showError = false, exception = null)
        }

        fun ofError(exception: Throwable?): SaveAnswersUIState {
            return SaveAnswersUIState(saved = false, showLoading = false, showError = true, exception = exception)
        }
    }
}