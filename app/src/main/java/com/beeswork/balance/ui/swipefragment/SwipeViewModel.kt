package com.beeswork.balance.ui.swipefragment

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class SwipeViewModel(
    private val swipeRepository: SwipeRepository,
    private val swipeMapper: SwipeMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val swipePageInvalidationLiveData by lazyDeferred {
        swipeRepository.getSwipePageInvalidationFlow().asLiveData()
    }

    @ExperimentalPagingApi
    fun initSwipePagingData(): LiveData<PagingData<SwipeDomain>> {
        return Pager(
            config = swipePagingConfig,
            remoteMediator = SwipeRemoteMediator(swipeRepository)
        ) {
            SwipePagingSource(swipeRepository)
        }.flow.cachedIn(viewModelScope)
            .map { pagingData ->
                pagingData.map { swipeMapper.toSwipeDomain(it) }
            }
            .map { pagingData ->
                pagingData.insertHeaderItem(TerminalSeparatorType.FULLY_COMPLETE, SwipeDomain.header())
            }
            .asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    fun test() {
        swipeRepository.test()
    }

    companion object {
        private const val SWIPE_PAGE_SIZE = 100
        private const val SWIPE_PAGE_PREFETCH_DISTANCE = SWIPE_PAGE_SIZE
        private const val SWIPE_MAX_PAGE_SIZE = SWIPE_PAGE_PREFETCH_DISTANCE * 3 + SWIPE_PAGE_SIZE
        private val swipePagingConfig = PagingConfig(
            SWIPE_PAGE_SIZE,
            SWIPE_PAGE_PREFETCH_DISTANCE,
            false,
            SWIPE_PAGE_SIZE,
            SWIPE_MAX_PAGE_SIZE
        )
    }

}