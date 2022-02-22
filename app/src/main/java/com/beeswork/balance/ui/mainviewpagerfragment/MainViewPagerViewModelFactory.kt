package com.beeswork.balance.ui.mainviewpagerfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import kotlinx.coroutines.CoroutineDispatcher

class MainViewPagerViewModelFactory(
    private val matchRepository: MatchRepository,
    private val swipeRepository: SwipeRepository,
    private val swipeMapper: SwipeMapper,
    private val matchMapper: MatchMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewPagerViewModel(
            matchRepository,
            swipeRepository,
            swipeMapper,
            matchMapper,
            defaultDispatcher
        ) as T
    }
}