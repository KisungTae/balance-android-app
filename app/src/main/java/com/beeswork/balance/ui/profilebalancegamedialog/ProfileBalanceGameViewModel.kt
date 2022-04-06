package com.beeswork.balance.ui.profilebalancegamedialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.uistate.balancegame.FetchQuestionsUIState
import com.beeswork.balance.domain.uistate.balancegame.SaveAnswersUIState
import com.beeswork.balance.domain.usecase.balancegame.FetchRandomQuestionsUseCase
import com.beeswork.balance.domain.usecase.balancegame.SaveAnswersUseCase
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class ProfileBalanceGameViewModel(
    private val fetchRandomQuestionsUseCase: FetchRandomQuestionsUseCase,
    private val saveAnswersUseCase: SaveAnswersUseCase
) : BaseViewModel() {

    private val _fetchQuestionsUIStateLiveData = MutableLiveData<FetchQuestionsUIState>()
    val fetchQuestionsUIStateLiveData: LiveData<FetchQuestionsUIState> get() = _fetchQuestionsUIStateLiveData

    private val _saveAnswersUIStateLiveData = MutableLiveData<SaveAnswersUIState>()
    val saveAnswersUIStateLiveData: LiveData<SaveAnswersUIState> get() = _saveAnswersUIStateLiveData

    fun fetchRandomQuestions() {
        viewModelScope.launch {
            _fetchQuestionsUIStateLiveData.postValue(FetchQuestionsUIState.ofLoading())
            val response = fetchRandomQuestionsUseCase.invoke()
            val fetchRandomQuestionsUIState = if (response.isSuccess() && response.data != null) {
                FetchQuestionsUIState.ofSuccess(response.data, 0)
            } else {
                FetchQuestionsUIState.ofError(response.exception)
            }
            _fetchQuestionsUIStateLiveData.postValue(fetchRandomQuestionsUIState)
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