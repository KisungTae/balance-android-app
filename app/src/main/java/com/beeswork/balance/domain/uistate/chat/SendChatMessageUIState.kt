package com.beeswork.balance.domain.uistate.chat

import com.beeswork.balance.domain.uistate.BaseUIState

class SendChatMessageUIState(
    val clearChatMessageInput: Boolean,
    val showError: Boolean,
    val exception: Throwable?
) {

    companion object {
        fun ofSuccess(): SendChatMessageUIState {
            return SendChatMessageUIState(
                clearChatMessageInput = true,
                showError = false,
                exception = null
            )
        }

        fun ofError(clearChatMessageInput: Boolean, exception: Throwable?): SendChatMessageUIState {
            return SendChatMessageUIState(
                clearChatMessageInput = clearChatMessageInput,
                showError = true,
                exception = exception
            )
        }
    }
}