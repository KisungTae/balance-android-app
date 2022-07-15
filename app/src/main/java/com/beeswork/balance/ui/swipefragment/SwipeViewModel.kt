package com.beeswork.balance.ui.swipefragment

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.domain.uistate.swipe.SwipeUIState
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.ui.common.paging.ItemKeyPager
import com.beeswork.balance.ui.common.paging.PagingMediator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class SwipeViewModel(
    private val swipeRepository: SwipeRepository,
    private val swipeMapper: SwipeMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val swipePageInvalidationLiveData by lazyDeferred {
        swipeRepository.getSwipePageInvalidationFlow().asLiveData()
    }

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow("")
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<String> = _uiState

//    @ExperimentalPagingApi
//    fun initSwipePagingData(): LiveData<PagingData<SwipeItemUIState>> {
//        return Pager(
//            config = swipePagingConfig,
//            remoteMediator = SwipeRemoteMediator(swipeRepository)
//        ) {
//            SwipePagingSource(swipeRepository)
//        }.flow.cachedIn(viewModelScope)
//            .map { pagingData ->
//                pagingData.map { swipe ->
//                    swipeMapper.toSwipeItemUIState(swipe, preferenceProvider.getPhotoDomain())
//                }
//            }
//            .map { pagingData ->
//                pagingData.insertHeaderItem(TerminalSeparatorType.FULLY_COMPLETE, SwipeItemUIState.asHeader())
//            }
//            .asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
//    }

    fun getPagingMediator(): PagingMediator<SwipeUIState> {
        return ItemKeyPager(
            SWIPE_PAGE_SIZE,
            SWIPE_NUM_OF_PAGES_TO_KEEP,
            SwipePagingSource(swipeRepository, swipeMapper),
            viewModelScope
        ).pagingMediator
    }

    fun test() {
        viewModelScope.launch {
            _uiState.emit("abc")
        }


        viewModelScope.launch {
            swipeRepository.test()
        }
    }

    companion object {
        private const val SWIPE_PAGE_SIZE = 30
        private const val SWIPE_NUM_OF_PAGES_TO_KEEP = 1

        //        private const val SWIPE_PAGE_PREFETCH_DISTANCE = SWIPE_PAGE_SIZE
//        private const val SWIPE_PAGE_PREFETCH_DISTANCE = SWIPE_PAGE_SIZE
//        private const val SWIPE_MAX_PAGE_SIZE = SWIPE_PAGE_PREFETCH_DISTANCE * 2 + SWIPE_PAGE_SIZE

        //        private const val SWIPE_MAX_PAGE_SIZE = 100
//        private val swipePagingConfig = PagingConfig(
//            SWIPE_PAGE_SIZE,
//            SWIPE_PAGE_PREFETCH_DISTANCE,
//            false,
//            SWIPE_PAGE_SIZE,
//            SWIPE_MAX_PAGE_SIZE
//        )
    }

}