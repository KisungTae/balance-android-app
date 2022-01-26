package com.beeswork.balance.ui.swipe

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import com.beeswork.balance.internal.mapper.swipe.CardMapper
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.ui.swipe.card.CardDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class SwipeViewModel(
    private val swipeRepository: SwipeRepository,
    private val settingRepository: SettingRepository,
    private val cardMapper: CardMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val locationPermissionResultLiveData by viewModelLazyDeferred {
        settingRepository.getLocationPermissionResultFlow().asLiveData()
    }

    private val _fetchCards = MutableLiveData<Resource<List<CardDomain>>>()
    val fetchCards: LiveData<Resource<List<CardDomain>>> get() = _fetchCards

    private var fetchingCards = false

//    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
//        fetchingCards = false
//        throw throwable
//    }

    fun fetchCards() {
        viewModelScope.launch {
//            if (fetchingCards) return@launch
//            settingRepository.syncLocation()
//            fetchingCards = true
//            _fetchCards.postValue(Resource.loading())
//            val response = swipeRepository.fetchCards().let {
//                it.mapData(it.data?.cardDTOs?.map { cardDTO -> cardMapper.toCardDomain(cardDTO) })
//            }
//            fetchingCards = false
//            _fetchCards.postValue(response)


            _fetchCards.postValue(Resource.error(NoInternetConnectivityException()))
        }

    }
}