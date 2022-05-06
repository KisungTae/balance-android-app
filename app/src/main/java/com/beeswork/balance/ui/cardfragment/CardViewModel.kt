package com.beeswork.balance.ui.cardfragment

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.internal.mapper.card.CardFilterMapper
import com.beeswork.balance.internal.mapper.card.CardMapper
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.domain.uistate.card.FetchCardsUIState
import com.beeswork.balance.domain.usecase.card.FetchCardsUseCase
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardViewModel(
    private val fetchCardsUseCase: FetchCardsUseCase,
    private val cardRepository: CardRepository,
    private val settingRepository: SettingRepository,
    private val cardMapper: CardMapper,
    private val cardFilterMapper: CardFilterMapper,
    private val preferenceProvider: PreferenceProvider,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val cardFilterInvalidationLiveData by viewModelLazyDeferred {
        cardRepository.getCardFilterInvalidationFlow().asLiveData()
    }

    val locationGrantedLiveData by viewModelLazyDeferred {
        settingRepository.getLocationGrantedFlow().map { granted ->
            granted ?: false
        }.asLiveData()
    }

    private val _fetchCardsUIStateLiveData = MutableLiveData<FetchCardsUIState>()
    val fetchCardsUIStateLiveData: LiveData<FetchCardsUIState> get() = _fetchCardsUIStateLiveData

    private var fetchingCards = false

    fun fetchCards(resetPage: Boolean) {
        viewModelScope.launch {
            if (fetchingCards) {
                return@launch
            }
            fetchingCards = true
            _fetchCardsUIStateLiveData.postValue(FetchCardsUIState.ofLoading())
            val response = fetchCardsUseCase.invoke(resetPage)
            val fetchCardsUIState = if (response.isSuccess() && response.data != null) {
                withContext(defaultDispatcher) {
                    val cardItemUIStates = response.data.map { card ->
                        cardMapper.toCardItemUIState(card, preferenceProvider.getPhotoDomain())
                    }
                    FetchCardsUIState.ofSuccess(cardItemUIStates)
                }
            } else {
                FetchCardsUIState.ofError(response.exception)
            }
            fetchingCards = false
            _fetchCardsUIStateLiveData.postValue(fetchCardsUIState)
        }

    }
}