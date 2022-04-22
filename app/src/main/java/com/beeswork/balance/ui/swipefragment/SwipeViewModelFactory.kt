package com.beeswork.balance.ui.swipefragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineDispatcher

class SwipeViewModelFactory(
    private val swipeRepository: SwipeRepository,
    private val swipeMapper: SwipeMapper,
    private val preferenceProvider: PreferenceProvider,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SwipeViewModel(swipeRepository, swipeMapper, preferenceProvider, defaultDispatcher) as T
    }
}