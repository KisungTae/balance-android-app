package com.beeswork.balance.domain.uistate.balancegame

import com.beeswork.balance.domain.uistate.match.MatchNotificationUIState
import com.beeswork.balance.internal.constant.ClickOutcome

data class ClickUIState(
    val clickOutcome: ClickOutcome?,
    val matchNotificationUIState: MatchNotificationUIState?,
    val showLoading: Boolean,
    val showError: Boolean,
    val exception: Throwable?
) {


    companion object {

        fun ofSuccess(clickOutcome: ClickOutcome, matchNotificationUIState: MatchNotificationUIState?): ClickUIState {
            return ClickUIState(clickOutcome, matchNotificationUIState, showLoading = false, showError = false, exception = null)
        }

        fun ofLoading(): ClickUIState {
            return ClickUIState(
                clickOutcome = null,
                matchNotificationUIState = null,
                showLoading = true,
                showError = false,
                exception = null
            )
        }

        fun ofError(exception: Throwable?): ClickUIState {
            return ClickUIState(
                clickOutcome = null,
                matchNotificationUIState = null,
                showLoading = false,
                showError = true,
                exception
            )
        }
    }
}