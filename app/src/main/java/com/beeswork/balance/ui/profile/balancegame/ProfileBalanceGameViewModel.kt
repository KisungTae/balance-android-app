package com.beeswork.balance.ui.profile.balancegame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.profile.QuestionMapper
import com.beeswork.balance.internal.util.safeLaunch

class ProfileBalanceGameViewModel(
    private val profileRepository: ProfileRepository,
    private val questionMapper: QuestionMapper
) : ViewModel() {

    private val _fetchQuestionsLiveData = MutableLiveData<Resource<List<QuestionDomain>>>()
    val fetchQuestionsLiveData: LiveData<Resource<List<QuestionDomain>>> get() = _fetchQuestionsLiveData

    private val _saveAnswersLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveAnswersLiveData: LiveData<Resource<EmptyResponse>> = _saveAnswersLiveData

    fun fetchQuestions() {
        viewModelScope.safeLaunch(_fetchQuestionsLiveData) {
            _fetchQuestionsLiveData.postValue(Resource.loading())
            val response = profileRepository.fetchQuestions().let {
                it.mapData(it.data?.map { questionDTO -> questionMapper.toQuestionDomain(questionDTO) })
            }
            _fetchQuestionsLiveData.postValue(response)
        }
    }

    fun saveQuestions(answers: Map<Int, Boolean>) {
        viewModelScope.safeLaunch(_saveAnswersLiveData) {
            _saveAnswersLiveData.postValue(Resource.loading())
            _saveAnswersLiveData.postValue(profileRepository.saveAnswers(answers))
        }
    }

}