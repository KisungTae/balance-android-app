package com.beeswork.balance.ui.swipe.balancegame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.PushType
import com.beeswork.balance.internal.mapper.profile.QuestionMapper
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.ui.profile.balancegame.QuestionDomain
import kotlinx.coroutines.launch
import java.util.*

class SwipeBalanceGameViewModel(
    private val swipeRepository: SwipeRepository,
    private val matchRepository: MatchRepository,
    private val questionMapper: QuestionMapper
) : BaseViewModel() {

    private val _swipeLiveData = MutableLiveData<Resource<List<QuestionDomain>>>()
    val swipeLiveData: LiveData<Resource<List<QuestionDomain>>> get() = _swipeLiveData

    private val _clickLiveData = MutableLiveData<Resource<PushType>>()
    val clickLiveData: LiveData<Resource<PushType>> = _clickLiveData

    fun click(swipedId: UUID, answers: Map<Int, Boolean>) {
        viewModelScope.launch {
            _clickLiveData.postValue(Resource.loading())
            _clickLiveData.postValue(matchRepository.click(swipedId, answers))
        }
    }

    fun swipe(swipedId: UUID) {
        viewModelScope.launch {
            _swipeLiveData.postValue(Resource.loading())
            val response = swipeRepository.swipe(swipedId).let {
                it.mapData(it.data?.map { questionDTO -> questionMapper.toQuestionDomain(questionDTO) })
            }
            _swipeLiveData.postValue(response)
        }
    }
}