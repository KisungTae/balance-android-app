package com.beeswork.balance.domain.uistate.balancegame

class FetchQuestionsUIState(
    val questionItemUIStates: List<QuestionItemUIState>?,
    val showLoading: Boolean,
    val showError: Boolean,
    val exception: Throwable?
) {

    companion object {

        fun ofSuccess(questionItemUIStates: List<QuestionItemUIState>): FetchQuestionsUIState {
            return FetchQuestionsUIState(questionItemUIStates, showLoading = false, showError = false, exception = null)
        }

        fun ofLoading(): FetchQuestionsUIState {
            return FetchQuestionsUIState(null, showLoading = true, showError = false, exception = null)
        }

        fun ofError(exception: Throwable?): FetchQuestionsUIState {
            return FetchQuestionsUIState(null, showLoading = false, showError = true, exception = exception)
        }
    }


}