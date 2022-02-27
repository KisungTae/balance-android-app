package com.beeswork.balance.ui.cardfragment.balancegame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.ClickDTO
import com.beeswork.balance.internal.constant.ClickResult
import com.beeswork.balance.internal.mapper.profile.QuestionMapper
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.ui.common.QuestionDomain
import kotlinx.coroutines.launch
import java.util.*

class CardBalanceGameViewModel(
    private val cardRepository: CardRepository,
    private val matchRepository: MatchRepository,
    private val questionMapper: QuestionMapper
) : BaseViewModel() {

    private val _likeLiveData = MutableLiveData<Resource<List<QuestionDomain>>>()
    val likeLiveData: LiveData<Resource<List<QuestionDomain>>> get() = _likeLiveData

    private val _clickLiveData = MutableLiveData<Resource<ClickDTO>>()
    val clickLiveData: LiveData<Resource<ClickDTO>> = _clickLiveData

    fun click(swipedId: UUID, answers: Map<Int, Boolean>) {
        viewModelScope.launch {
            _clickLiveData.postValue(Resource.loading())
            _clickLiveData.postValue(matchRepository.click(swipedId, answers))
        }
    }

    fun like(swipedId: UUID) {
        viewModelScope.launch {
            _likeLiveData.postValue(Resource.loading())
            val response = cardRepository.like(swipedId).map { questionDTOs ->
                questionDTOs?.map { questionDTO ->
                    questionMapper.toQuestionDomain(questionDTO)
                }
            }
            _likeLiveData.postValue(response)
        }
    }
}