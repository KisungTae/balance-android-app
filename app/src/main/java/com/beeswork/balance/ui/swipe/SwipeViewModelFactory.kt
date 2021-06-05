package com.beeswork.balance.ui.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.internal.mapper.swipe.CardMapper
import kotlinx.coroutines.CoroutineDispatcher

class SwipeViewModelFactory(
    private val swipeRepository: SwipeRepository,
    private val settingRepository: SettingRepository,
    private val cardMapper: CardMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SwipeViewModel(swipeRepository, settingRepository, cardMapper, defaultDispatcher) as T
    }
}