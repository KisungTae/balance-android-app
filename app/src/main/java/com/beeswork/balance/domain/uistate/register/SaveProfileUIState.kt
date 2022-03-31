package com.beeswork.balance.domain.uistate.register

data class SaveProfileUIState(
    val saved: Boolean,
    val showError: Boolean,
    val exception: Throwable?
) {

    companion object {
        fun ofSuccess(): SaveProfileUIState {
            return SaveProfileUIState(saved = true, showError = false, exception = null)
        }

        fun ofError(exception: Throwable?): SaveProfileUIState {
            return SaveProfileUIState(saved = false, showError = true, exception = exception)
        }
    }
}