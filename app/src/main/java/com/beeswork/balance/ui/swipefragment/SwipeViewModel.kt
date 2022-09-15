package com.beeswork.balance.ui.swipefragment

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.domain.uistate.swipe.SwipeUIState
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.ui.common.page.*
import kotlinx.coroutines.CoroutineDispatcher


class SwipeViewModel(
    private val swipeRepository: SwipeRepository,
    private val swipeMapper: SwipeMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    // todo: check if it only accepts most recent values, like liveData or flow or it consumes all values sent from buffer channel
    val swipePageInvalidationLiveData by lazyDeferred {
        swipeRepository.getSwipePageInvalidationFlow().asLiveData()
    }

    fun initPageMediator(): PageMediator<SwipeUIState> {
        return Pager(
            SWIPE_PAGE_SIZE,
            SWIPE_MAX_PAGE_SIZE,
            SwipePageSource(swipeRepository, swipeMapper),
            viewModelScope
        ).withHeader {
            SwipeUIState.Header
        }.withPageLoadStateLoading { pageLoadType ->
            SwipeUIState.PageLoadState(PageLoadStatus.Loading(pageLoadType))
        }.withPageLoadStateError { pageLoadType, exception ->
            SwipeUIState.PageLoadState(PageLoadStatus.Error(pageLoadType, exception))
        }
    }

    fun test() {
    }

    companion object {
        private const val SWIPE_PAGE_SIZE = 50
        private const val SWIPE_MAX_PAGE_SIZE = 2 * SWIPE_PAGE_SIZE
    }

}