package com.beeswork.balance.ui.swipe

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.mapper.swipe.CardMapper
import com.beeswork.balance.internal.util.safeLaunch

class SwipeViewModel(
    private val swipeRepository: SwipeRepository,
    private val settingRepository: SettingRepository,
    private val cardMapper: CardMapper
) : ViewModel() {

    private val _fetchCards = MutableLiveData<Resource<List<CardDomain>>>()
    val fetchCards: LiveData<Resource<List<CardDomain>>> get() = _fetchCards

    private var fetchingCards = false

    fun fetchCards() {
        viewModelScope.safeLaunch(_fetchCards, { fetchingCards = false }) {
            if (!fetchingCards) {
                fetchingCards = true
                _fetchCards.postValue(Resource.loading())
                settingRepository.syncLocation()
                val response = swipeRepository.fetchCards().let {
                    it.mapData(it.data?.cardDTOs?.map { cardDTO -> cardMapper.toCardDomain(cardDTO) })
                }
                fetchingCards = false
                _fetchCards.postValue(response)
            }
        }

    }
}