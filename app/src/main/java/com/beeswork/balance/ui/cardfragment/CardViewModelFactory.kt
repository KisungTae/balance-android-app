package com.beeswork.balance.ui.cardfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.domain.usecase.card.FetchCardsUseCase
import com.beeswork.balance.domain.usecase.card.IncrementReadByIndexUseCase
import com.beeswork.balance.internal.mapper.card.CardMapper
import kotlinx.coroutines.CoroutineDispatcher

class CardViewModelFactory(
    private val fetchCardsUseCase: FetchCardsUseCase,
    private val incrementReadByIndexUseCase: IncrementReadByIndexUseCase,
    private val cardRepository: CardRepository,
    private val settingRepository: SettingRepository,
    private val cardMapper: CardMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CardViewModel(
            fetchCardsUseCase,
            incrementReadByIndexUseCase,
            cardRepository,
            settingRepository,
            cardMapper,
            defaultDispatcher
        ) as T
    }
}