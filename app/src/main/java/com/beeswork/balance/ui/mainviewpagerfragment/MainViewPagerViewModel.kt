package com.beeswork.balance.ui.mainviewpagerfragment

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map

class MainViewPagerViewModel(
    private val matchRepository: MatchRepository,
    private val swipeRepository: SwipeRepository,
    private val swipeMapper: SwipeMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val swipeCountLiveData by viewModelLazyDeferred {
        swipeRepository.swipeCountFlow.asLiveData()
    }

    val newSwipeLiveData by viewModelLazyDeferred {
        swipeRepository.newSwipeFlow.map { swipe ->
            swipeMapper.toSwipeDomain(swipe)
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }





    val unreadMatchCount by viewModelLazyDeferred {
        matchRepository.getUnreadMatchCountFlow().asLiveData()
    }


}