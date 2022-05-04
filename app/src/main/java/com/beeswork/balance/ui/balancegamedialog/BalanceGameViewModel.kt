package com.beeswork.balance.ui.balancegamedialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.domain.uistate.balancegame.ClickUIState
import com.beeswork.balance.domain.uistate.balancegame.FetchQuestionsUIState
import com.beeswork.balance.domain.uistate.balancegame.FetchRandomQuestionUIState
import com.beeswork.balance.domain.uistate.balancegame.SaveAnswersUIState
import com.beeswork.balance.domain.usecase.balancegame.FetchQuestionsUseCase
import com.beeswork.balance.domain.usecase.balancegame.FetchRandomQuestionUseCase
import com.beeswork.balance.domain.usecase.balancegame.SaveAnswersUseCase
import com.beeswork.balance.domain.usecase.card.ClickUseCase
import com.beeswork.balance.domain.usecase.card.LikeUseCase
import com.beeswork.balance.domain.usecase.photo.GetProfilePhotoUseCase
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.mapper.profile.QuestionMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

class BalanceGameViewModel(
    private val fetchQuestionsUseCase: FetchQuestionsUseCase,
    private val saveAnswersUseCase: SaveAnswersUseCase,
    private val fetchRandomQuestionUseCase: FetchRandomQuestionUseCase,
    private val getProfilePhotoUseCase: GetProfilePhotoUseCase,
    private val likeUseCase: LikeUseCase,
    private val clickUseCase: ClickUseCase,
    private val preferenceProvider: PreferenceProvider,
    private val matchMapper: MatchMapper,
    private val questionMapper: QuestionMapper
) : BaseViewModel() {

    private val _fetchQuestionsUIStateLiveData = MutableLiveData<FetchQuestionsUIState>()
    val fetchQuestionsUIStateLiveData: LiveData<FetchQuestionsUIState> get() = _fetchQuestionsUIStateLiveData

    private val _saveAnswersUIStateLiveData = MutableLiveData<SaveAnswersUIState>()
    val saveAnswersUIStateLiveData: LiveData<SaveAnswersUIState> get() = _saveAnswersUIStateLiveData

    private val _fetchRandomQuestionUIStateLiveData = MutableLiveData<FetchRandomQuestionUIState>()
    val fetchRandomQuestionUIStateLiveData: LiveData<FetchRandomQuestionUIState> get() = _fetchRandomQuestionUIStateLiveData

    private val _clickUIStateLiveData = MutableLiveData<ClickUIState>()
    val clickUIStateLiveData: LiveData<ClickUIState> get() = _clickUIStateLiveData

    private val _profilePhotoUrlLiveData = MutableLiveData<String?>()
    val profilePhotoUrlLiveData: LiveData<String?> get() = _profilePhotoUrlLiveData


    fun fetchProfilePhotoUrl() {
        viewModelScope.launch {
            val profilePhoto = getProfilePhotoUseCase.invoke()
            val profilePhotoUrl = EndPoint.ofPhoto(
                preferenceProvider.getPhotoDomain(),
                profilePhoto?.accountId,
                profilePhoto?.key
            )
            _profilePhotoUrlLiveData.postValue(profilePhotoUrl)
        }
    }

    fun like(swipedId: UUID) {
        viewModelScope.launch {
            _fetchQuestionsUIStateLiveData.postValue(FetchQuestionsUIState.ofLoading())
            val response = likeUseCase.invoke(swipedId)
            handleFetchQuestionsResponse(response)
        }
    }

    fun click(swipedId: UUID, answers: Map<Int, Boolean>) {
        viewModelScope.launch {
            _clickUIStateLiveData.postValue(ClickUIState.ofLoading())
            val response = clickUseCase.invoke(swipedId, answers)
            val clickUIState = if (response.isSuccess() && response.data != null && response.data.match != null) {
                val matchNotificationUIState = matchMapper.toMatchNotificationUIState(
                    response.data.match,
                    preferenceProvider.getPhotoDomain()
                )
                ClickUIState.ofSuccess(response.data.clickOutcome, matchNotificationUIState)
            } else {
                ClickUIState.ofError(response.exception)
            }
            _clickUIStateLiveData.postValue(clickUIState)
        }
    }

    fun fetchRandomQuestion(questionIds: List<Int>) {
        viewModelScope.launch {
            _fetchRandomQuestionUIStateLiveData.postValue(FetchRandomQuestionUIState.ofLoading())
            val response = fetchRandomQuestionUseCase.invoke(questionIds)
            val fetchRandomQuestionUIState = if (response.isSuccess() && response.data != null) {
                FetchRandomQuestionUIState.ofSuccess(questionMapper.toQuestionItemUIState(response.data))
            } else {
                FetchRandomQuestionUIState.ofError(response.exception)
            }
            _fetchRandomQuestionUIStateLiveData.postValue(fetchRandomQuestionUIState)
        }
    }

    fun fetchQuestions() {
        viewModelScope.launch {
            _fetchQuestionsUIStateLiveData.postValue(FetchQuestionsUIState.ofLoading())
            val response = fetchQuestionsUseCase.invoke()
            handleFetchQuestionsResponse(response)
        }
    }

    private fun handleFetchQuestionsResponse(response: Resource<FetchQuestionsDTO>) {
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
