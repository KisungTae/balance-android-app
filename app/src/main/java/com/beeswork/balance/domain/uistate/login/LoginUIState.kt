package com.beeswork.balance.domain.uistate.login

class LoginUIState(
    val profileExists: Boolean,
    val shouldLogin: Boolean,
    val exception: Throwable?
) {

    companion object {

        fun ofSuccess(profileExists: Boolean): LoginUIState {
            return LoginUIState(
                profileExists = profileExists,
                shouldLogin = false,
                exception = null
            )
        }

        fun ofError(exception: Throwable?): LoginUIState {
            return LoginUIState(
                profileExists = false,
                shouldLogin = true,
                exception = exception
            )
        }
    }
}