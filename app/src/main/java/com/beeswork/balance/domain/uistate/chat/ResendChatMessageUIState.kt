package com.beeswork.balance.domain.uistate.chat

import com.beeswork.balance.domain.uistate.BaseUIState

class ResendChatMessageUIState(
    val showError: Boolean,
    val exception: Throwable?
) {
    companion object {
        fun ofSuccess(): ResendChatMessageUIState {
            return ResendChatMessageUIState(
                showError = false,
                exception = null
            )
        }

        fun ofError(exception: Throwable?): ResendChatMessageUIState {
            return ResendChatMessageUIState(
                showError = true,
                exception = exception
            )
        }
    }
}