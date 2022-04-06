package com.beeswork.balance.ui.balancegamedialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.uistate.balancegame.FetchQuestionsUIState
import com.beeswork.balance.domain.uistate.balancegame.SaveAnswersUIState
import com.beeswork.balance.domain.usecase.balancegame.FetchQuestionsUseCase
import com.beeswork.balance.domain.usecase.balancegame.SaveAnswersUseCase
import com.beeswork.balance.internal.mapper.profile.QuestionMapper
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class BalanceGameViewModel(
    private val fetchQuestionsUseCase: FetchQuestionsUseCase,
    private val saveAnswersUseCase: SaveAnswersUseCase,
    private val questionMapper: QuestionMapper
) : BaseViewModel() {

    private val _fetchQuestionsUIStateLiveData = MutableLiveData<FetchQuestionsUIState>()
    val fetchQuestionsUIStateLiveData: LiveData<FetchQuestionsUIState> get() = _fetchQuestionsUIStateLiveData

    private val _saveAnswersUIStateLiveData = MutableLiveData<SaveAnswersUIState>()
    val saveAnswersUIStateLiveData: LiveData<SaveAnswersUIState> get() = _saveAnswersUIStateLiveData

    fun fetchQuestions() {
        viewModelScope.launch {
            _fetchQuestionsUIStateLiveData.postValue(FetchQuestionsUIState.ofLoading())
            val response = fetchQuestionsUseCase.invoke()
            val fetchQuestionsUIState = if (response.isSuccess() && response.data != null) {
                val questionItemUIStates = response.data.questionDTOs.map { questionDTO ->
                    questionMapper.toQuestionItemUIState(questionDTO)
                }
                FetchQuestionsUIState.ofSuccess(questionItemUIStates, response.data.point)
            } else {
                FetchQuestionsUIState.ofError(response.exception)
            }
            _fetchQuestionsUIStateLiveData.postValue(fetchQuestionsUIState)
        }
    }

    fun saveAnswers(answers: Map<Int, Boolean>) {
        viewModelScope.launch {
            _saveAnswersUIStateLiveData.postValue(SaveAnswersUIState.ofLoading())
            val response = saveAnswersUseCase.invoke(answers)
            val saveAnswersUIState = if (response.isSuccess()) {
                SaveAnswersUIState.ofSuccess()
            } else {
                SaveAnswersUIState.ofError(response.exception)
            }
            _saveAnswersUIStateLiveData.postValue(saveAnswersUIState)
        }
    }
}
