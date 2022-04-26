package com.beeswork.balance.ui.cardfragment

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.mapper.card.CardFilterMapper
import com.beeswork.balance.internal.mapper.card.CardMapper
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.ui.cardfragment.card.CardDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class CardViewModel(
    private val cardRepository: CardRepository,
    private val settingRepository: SettingRepository,
    private val cardMapper: CardMapper,
    private val cardFilterMapper: CardFilterMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val cardFilterInvalidationLiveData by viewModelLazyDeferred {
        cardRepository.getCardFilterInvalidationFlow().asLiveData()
    }

    private val _fetchCards = MutableLiveData<Resource<List<CardDomain>>>()
    val fetchCards: LiveData<Resource<List<CardDomain>>> get() = _fetchCards

    private var fetchingCards = false

    fun fetchCards() {
        viewModelScope.launch {
//            if (fetchingCards) return@launch
//            settingRepository.syncLocation()
//            fetchingCards = true
//            _fetchCards.postValue(Resource.loading())
//            val response = cardRepository.fetchCards().map { fetchCardsDTO ->
//                fetchCardsDTO?.cardDTOs?.map { cardDTO ->
//                    cardMapper.toCardDomain(cardDTO)
//                }
//            }
//            fetchingCards = false
//            _fetchCards.postValue(response)
        }

    }
}