package com.beeswork.balance.domain.uistate.balancegame

class FetchRandomQuestionUIState(
    val questionItemUIState: QuestionItemUIState?,
    val showLoading: Boolean,
    val showError: Boolean,
    val exception: Throwable?
) {
    companion object {

        fun ofSuccess(questionItemUIState: QuestionItemUIState): FetchRandomQuestionUIState {
            return FetchRandomQuestionUIState(
                questionItemUIState = questionItemUIState,
                showLoading = false,
                showError = false,
                exception = null
            )
        }

        fun ofLoading(): FetchRandomQuestionUIState {
            return FetchRandomQuestionUIState(questionItemUIState = null, showLoading = true, showError = false, exception = null)
        }

        fun ofError(exception: Throwable?): FetchRandomQuestionUIState {
            return FetchRandomQuestionUIState(questionItemUIState = null, showLoading = false, showError = true, exception = exception)
        }
    }
}