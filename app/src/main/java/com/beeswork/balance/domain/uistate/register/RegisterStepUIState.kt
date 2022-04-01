package com.beeswork.balance.domain.uistate.register

data class RegisterStepUIState(
    val saved: Boolean,
    val showError: Boolean,
    val exception: Throwable?
) {

    companion object {
        fun ofSuccess(): RegisterStepUIState {
            return RegisterStepUIState(saved = true, showError = false, exception = null)
        }

        fun ofError(exception: Throwable?): RegisterStepUIState {
            return RegisterStepUIState(saved = false, showError = true, exception = exception)
        }
    }
}