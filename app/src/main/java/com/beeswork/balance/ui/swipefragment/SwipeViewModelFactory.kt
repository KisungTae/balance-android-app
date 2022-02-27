package com.beeswork.balance.ui.swipefragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import kotlinx.coroutines.CoroutineDispatcher

class SwipeViewModelFactory(
    private val swipeRepository: SwipeRepository,
    private val swipeMapper: SwipeMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SwipeViewModel(swipeRepository, swipeMapper, defaultDispatcher) as T
    }
}