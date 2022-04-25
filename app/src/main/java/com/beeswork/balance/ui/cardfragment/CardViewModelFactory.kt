package com.beeswork.balance.ui.cardfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.internal.mapper.card.CardFilterMapper
import com.beeswork.balance.internal.mapper.card.CardMapper
import kotlinx.coroutines.CoroutineDispatcher

class CardViewModelFactory(
    private val cardRepository: CardRepository,
    private val settingRepository: SettingRepository,
    private val cardMapper: CardMapper,
    private val cardFilterMapper: CardFilterMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CardViewModel(cardRepository, settingRepository, cardMapper, cardFilterMapper, defaultDispatcher) as T
    }
}