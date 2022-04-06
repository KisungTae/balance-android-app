package com.beeswork.balance.domain.uistate.balancegame

class FetchQuestionsUIState(
    val questionItemUIStates: List<QuestionItemUIState>?,
    val point: Int?,
    val showLoading: Boolean,
    val showError: Boolean,
    val exception: Throwable?
) {
    companion object {

        fun ofSuccess(questionItemUIStates: List<QuestionItemUIState>, point: Int): FetchQuestionsUIState {
            return FetchQuestionsUIState(questionItemUIStates, point, showLoading = false, showError = false, exception = null)
        }

        fun ofLoading(): FetchQuestionsUIState {
            return FetchQuestionsUIState(null, null, showLoading = true, showError = false, exception = null)
        }

        fun ofError(exception: Throwable?): FetchQuestionsUIState {
            return FetchQuestionsUIState(null, null, showLoading = false, showError = true, exception = exception)
        }
    }
}