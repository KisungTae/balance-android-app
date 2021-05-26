package com.beeswork.balance.ui.balancegame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.PushType
import com.beeswork.balance.internal.mapper.profile.QuestionMapper
import com.beeswork.balance.internal.util.safeLaunch
import com.beeswork.balance.ui.profile.QuestionDomain
import java.util.*

class BalanceGameDialogViewModel(
    private val swipeRepository: SwipeRepository,
    private val profileRepository: ProfileRepository,
    private val matchRepository: MatchRepository,
    private val questionMapper: QuestionMapper
) : ViewModel() {

    private val _swipeLiveData = MutableLiveData<Resource<List<QuestionDomain>>>()
    val swipeLiveData: LiveData<Resource<List<QuestionDomain>>> get() = _swipeLiveData

    private val _clickLiveData = MutableLiveData<Resource<PushType>>()
    val clickLiveData: LiveData<Resource<PushType>> = _clickLiveData

    fun click(swipedId: UUID, answers: Map<Int, Boolean>) {
        viewModelScope.safeLaunch(_clickLiveData) {
            _clickLiveData.postValue(Resource.loading())
            _clickLiveData.postValue(matchRepository.click(swipedId, answers))
        }
    }

    fun swipe(swipedId: UUID) {
        viewModelScope.safeLaunch(_swipeLiveData) {
            _swipeLiveData.postValue(Resource.loading())
            val response = swipeRepository.swipe(swipedId).let {
                it.mapData(it.data?.map { questionDTO -> questionMapper.toQuestionDomain(questionDTO) })
            }
            _swipeLiveData.postValue(response)
        }
    }
}