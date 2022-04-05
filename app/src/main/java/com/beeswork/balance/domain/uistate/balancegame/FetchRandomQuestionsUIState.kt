package com.beeswork.balance.domain.uistate.balancegame

class FetchRandomQuestionsUIState(
    val questionItemUIStates: List<QuestionItemUIState>?,
    val showLoading: Boolean,
    val showError: Boolean,
    val exception: Throwable?
) {

    companion object {

        fun ofSuccess(questionItemUIStates: List<QuestionItemUIState>): FetchRandomQuestionsUIState {
            return FetchRandomQuestionsUIState(questionItemUIStates, showLoading = false, showError = false, exception = null)
        }

        fun ofLoading(): FetchRandomQuestionsUIState {
            return FetchRandomQuestionsUIState(null, showLoading = true, showError = false, exception = null)
        }

        fun ofError(exception: Throwable?): FetchRandomQuestionsUIState {
            return FetchRandomQuestionsUIState(null, showLoading = false, showError = true, exception = exception)
        }
    }


}