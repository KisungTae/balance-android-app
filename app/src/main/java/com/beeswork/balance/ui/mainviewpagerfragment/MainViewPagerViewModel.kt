package com.beeswork.balance.ui.mainviewpagerfragment

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.domain.uistate.tabcount.TabCountUIState
import com.beeswork.balance.domain.usecase.tabcount.GetTabCountFlowUseCase
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map

class MainViewPagerViewModel(
    private val tabCountFlowUseCase: GetTabCountFlowUseCase,
    private val matchRepository: MatchRepository,
    private val swipeRepository: SwipeRepository,
    private val swipeMapper: SwipeMapper,
    private val matchMapper: MatchMapper,
    private val preferenceProvider: PreferenceProvider,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val tabCountUIStatesLiveData by viewModelLazyDeferred {
        tabCountFlowUseCase.invoke().map { tabCounts ->
            tabCounts.map { tabCount ->
                TabCountUIState(tabCount.tabPosition, tabCount.count)
            }
        }.asLiveData()
    }

    val newSwipeNotificationUIStateLiveData by viewModelLazyDeferred {
        swipeRepository.newSwipeFlow.map { swipe ->
            swipeMapper.toSwipeNotificationUIState(swipe, preferenceProvider.getPhotoDomain())
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    val newMatchNotificationUIStateLiveData by viewModelLazyDeferred {
        matchRepository.newMatchFlow.map { match ->
            matchMapper.toMatchNotificationUIState(match, preferenceProvider.getPhotoDomain())
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

}