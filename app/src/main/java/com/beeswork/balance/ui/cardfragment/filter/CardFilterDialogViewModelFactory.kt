package com.beeswork.balance.ui.cardfragment.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.internal.mapper.card.CardFilterMapper

class CardFilterDialogViewModelFactory(
    private val cardRepository: CardRepository,
    private val cardFilterMapper: CardFilterMapper
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CardFilterDialogViewModel(cardRepository, cardFilterMapper) as T
    }
}