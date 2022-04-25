package com.beeswork.balance.ui.cardfragment.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.domain.uistate.card.CardFilterUIState
import com.beeswork.balance.domain.uistate.card.SaveCardFilterUIState
import com.beeswork.balance.domain.usecase.card.GetCardFilterUseCase
import com.beeswork.balance.domain.usecase.card.SaveCardFilterUseCase
import com.beeswork.balance.internal.mapper.card.CardFilterMapper
import kotlinx.coroutines.launch

class CardFilterDialogViewModel(
    private val getCardFilterUseCase: GetCardFilterUseCase,
    private val saveCardFilterUseCase: SaveCardFilterUseCase,
    private val cardFilterMapper: CardFilterMapper
): ViewModel() {

    private val _cardFilterUIStateLiveData = MutableLiveData<CardFilterUIState>()
    val cardFilterUIStateLiveData: LiveData<CardFilterUIState> get() = _cardFilterUIStateLiveData

    private val _saveCardFilterUIStateLiveData = MutableLiveData<SaveCardFilterUIState>()
    val saveCardFilterUIStateLiveData: LiveData<SaveCardFilterUIState> get() = _saveCardFilterUIStateLiveData

    fun fetchCardFilter() {
        viewModelScope.launch {
            val cardFilter = getCardFilterUseCase.invoke()
            val cardFilterUIState = if (cardFilter == null) {
                CardFilterUIState()
            } else {
                cardFilterMapper.toCardFilterUIState(cardFilter)
            }
            _cardFilterUIStateLiveData.postValue(cardFilterUIState)
        }
    }

    fun saveCardFilter(gender: Boolean, minAge: Int, maxAge: Int, distance: Int) {
        viewModelScope.launch {
            val response = saveCardFilterUseCase.invoke(gender, minAge, maxAge, distance)
            val saveCardFilterUIState = if (response.isSuccess()) {
                SaveCardFilterUIState.ofSuccess()
            } else {
                SaveCardFilterUIState.ofError(response.exception)
            }
            _saveCardFilterUIStateLiveData.postValue(saveCardFilterUIState)
        }
    }
}