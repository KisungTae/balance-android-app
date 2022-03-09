package com.beeswork.balance.domain.uistate.chat

import com.beeswork.balance.domain.uistate.BaseUIState

class SendChatMessageUIState(
    val clearChatMessageInput: Boolean,
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
) : BaseUIState(showLoading, showError, shouldLogout, exception) {

    companion object {
        fun ofSuccess(): SendChatMessageUIState {
            return SendChatMessageUIState(
                clearChatMessageInput = true,
                showLoading = false,
                showError = false,
                shouldLogout = false,
                exception = null
            )
        }

        fun ofError(shouldLogout: Boolean, exception: Throwable?): SendChatMessageUIState {
            return SendChatMessageUIState(
                clearChatMessageInput = false,
                showLoading = false,
                showError = true,
                shouldLogout = shouldLogout,
                exception = exception
            )
        }
    }
}