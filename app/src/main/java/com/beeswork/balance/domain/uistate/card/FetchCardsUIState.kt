package com.beeswork.balance.domain.uistate.card

import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.internal.constant.ExceptionCode

class FetchCardsUIState(
    val cardItemUIStates: List<CardItemUIState>?,
    showLoading: Boolean,
    showError: Boolean,
    shouldLogout: Boolean,
    exception: Throwable?
) : UIState(showLoading, showError, shouldLogout, exception) {

    companion object {
        fun ofSuccess(cardItemUIStates: List<CardItemUIState>): FetchCardsUIState {
            return FetchCardsUIState(cardItemUIStates, showLoading = false, showError = false, shouldLogout = false, exception = null)
        }

        fun ofLoading(): FetchCardsUIState {
            return FetchCardsUIState(null, showLoading = true, showError = false, shouldLogout = false, exception = null)
        }

        fun ofError(exception: Throwable?): FetchCardsUIState {
            return FetchCardsUIState(
                null,
                showLoading = false,
                showError = true,
                shouldLogout = ExceptionCode.isLoginException(exception),
                exception = exception
            )
        }
    }

}