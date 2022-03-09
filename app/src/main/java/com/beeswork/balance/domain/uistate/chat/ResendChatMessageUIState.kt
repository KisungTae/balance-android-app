package com.beeswork.balance.domain.uistate.chat

import com.beeswork.balance.domain.uistate.BaseUIState

class ResendChatMessageUIState(
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
): BaseUIState(showLoading, showError, shouldLogout, exception) {
    companion object {
        fun ofSuccess(): ResendChatMessageUIState {
            return ResendChatMessageUIState(
                showLoading = false,
                showError = false,
                shouldLogout = false,
                exception = null
            )
        }

        fun ofError(shouldLogout: Boolean, exception: Throwable?): ResendChatMessageUIState {
            return ResendChatMessageUIState(
                showLoading = false,
                showError = true,
                shouldLogout = shouldLogout,
                exception = exception
            )
        }
    }
}