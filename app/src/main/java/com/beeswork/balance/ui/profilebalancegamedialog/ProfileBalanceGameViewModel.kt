package com.beeswork.balance.ui.profilebalancegamedialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.beeswork.balance.domain.uistate.balancegame.FetchRandomQuestionsUIState
import com.beeswork.balance.domain.uistate.balancegame.SaveAnswersUIState
import com.beeswork.balance.domain.usecase.balancegame.FetchRandomQuestionsUseCase
import com.beeswork.balance.domain.usecase.balancegame.SaveAnswersUseCase
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class ProfileBalanceGameViewModel(
    private val fetchRandomQuestionsUseCase: FetchRandomQuestionsUseCase,
    private val saveAnswersUseCase: SaveAnswersUseCase
) : BaseViewModel() {

//    private val _fetchQuestionsLiveData = MutableLiveData<Resource<List<QuestionDomain>>>()
//    val fetchQuestionsLiveData: LiveData<Resource<List<QuestionDomain>>> get() = _fetchQuestionsLiveData
//
//    private val _saveAnswersLiveData = MutableLiveData<Resource<EmptyResponse>>()
//    val saveAnswersLiveData: LiveData<Resource<EmptyResponse>> = _saveAnswersLiveData

    private val _fetchRandomQuestionsUIStateLiveData = MutableLiveData<FetchRandomQuestionsUIState>()
    val fetchRandomQuestionsUIStateLiveData: LiveData<FetchRandomQuestionsUIState> get() = _fetchRandomQuestionsUIStateLiveData

    private val _saveAnswersUIStateLiveData = MutableLiveData<SaveAnswersUIState>()
    val saveAnswersUIStateLiveData: LiveData<SaveAnswersUIState> get() = _saveAnswersUIStateLiveData

    fun fetchRandomQuestions() {
        viewModelScope.launch {
            _fetchRandomQuestionsUIStateLiveData.postValue(FetchRandomQuestionsUIState.ofLoading())
            val response = fetchRandomQuestionsUseCase.invoke()
            val fetchRandomQuestionsUIState = if (response.isSuccess() && response.data != null) {
                FetchRandomQuestionsUIState.ofSuccess(response.data)
            } else {
                FetchRandomQuestionsUIState.ofError(response.exception)
            }
            _fetchRandomQuestionsUIStateLiveData.postValue(fetchRandomQuestionsUIState)
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


//    fun fetchQuestions() {
//        viewModelScope.launch {
//            _fetchQuestionsLiveData.postValue(Resource.loading())
//            val response = profileRepository.fetchQuestions().map { questionDTOs ->
//                questionDTOs?.map { questionDTO ->
//                    questionMapper.toQuestionDomain(questionDTO)
//                }
//            }
//            _fetchQuestionsLiveData.postValue(response)
//        }
//    }
//
//    fun saveQuestions(answers: Map<Int, Boolean>) {
//        viewModelScope.launch {
//            _saveAnswersLiveData.postValue(Resource.loading())
//            _saveAnswersLiveData.postValue(profileRepository.saveAnswers(answers))
//        }
//    }

}