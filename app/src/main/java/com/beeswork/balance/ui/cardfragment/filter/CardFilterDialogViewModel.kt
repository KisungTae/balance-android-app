package com.beeswork.balance.ui.cardfragment.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.domain.uistate.card.CardFilterUIState
import com.beeswork.balance.internal.mapper.card.CardFilterMapper
import kotlinx.coroutines.launch

class CardFilterDialogViewModel(
    private val cardRepository: CardRepository,
    private val cardFilterMapper: CardFilterMapper
): ViewModel() {

    private val _cardFilterLiveData = MutableLiveData<CardFilterUIState>()
    val cardFilterLiveData: LiveData<CardFilterUIState> get() = _cardFilterLiveData

    private val _saveCardFilterLiveData = MutableLiveData<Any?>()
    val saveCardFilterLiveData: LiveData<Any?> get() = _saveCardFilterLiveData

    fun fetchCardFilter() {
        viewModelScope.launch {
            val cardFilter = cardRepository.getCardFilter()
            _cardFilterLiveData.postValue(cardFilterMapper.toCardFilterUIState(cardFilter))
        }
    }

    fun saveCardFilter(gender: Boolean, minAge: Int, maxAge: Int, distance: Int) {
        viewModelScope.launch {
            cardRepository.saveCardFilter(gender, minAge, maxAge, distance)
        }
    }
}