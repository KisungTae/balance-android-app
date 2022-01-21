package com.beeswork.balance.ui.click

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.click.ClickMapper
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class ClickViewModel(
    private val clickRepository: ClickRepository,
    private val clickMapper: ClickMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val clickInvalidation by viewModelLazyDeferred {
        clickRepository.getClickInvalidationFlow().asLiveData()
    }

    val newClickLiveData by viewModelLazyDeferred {
        clickRepository.newClickFlow.map { clickMapper.toClickDomain(it) }.asLiveData()
    }

    private val _fetchClicks = MutableLiveData<Resource<EmptyResponse>>()
    val fetchClicks: LiveData<Resource<EmptyResponse>> get() = _fetchClicks

    private var fetchingClicks = false

    fun initClickPagingData(): LiveData<PagingData<ClickDomain>> {
        return Pager(
            pagingConfig,
            null,
//            ClickRemoteMediator(),
            { ClickPagingSource(clickRepository) }
        ).flow.cachedIn(viewModelScope)
            .map { pagingData ->
                pagingData.map { clickMapper.toClickDomain(it) }
            }
            .map { pagingData ->
                pagingData.insertHeaderItem(TerminalSeparatorType.FULLY_COMPLETE, ClickDomain.header())
            }
            .asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    fun fetchClicks() {
        viewModelScope.launch {
            if (!fetchingClicks) {
                fetchingClicks = true
                _fetchClicks.postValue(Resource.loading())
                _fetchClicks.postValue(clickRepository.fetchClicks())
                fetchingClicks = false
            }
        }
    }

    fun test() {
        clickRepository.test()
    }


//    fun fetchClicks() {
//        balanceRepository.fetchClickedList()
//    }
//
//    fun swipe(swipeId: String) {
//        balanceRepository.swipe(null, swipeId)
//    }

    companion object {
        private const val CLICK_PAGE_SIZE = 30
        private const val CLICK_PAGE_PREFETCH_DISTANCE = CLICK_PAGE_SIZE
        private const val CLICK_MAX_PAGE_SIZE = CLICK_PAGE_PREFETCH_DISTANCE * 3 + CLICK_PAGE_SIZE
        private val pagingConfig = PagingConfig(
            CLICK_PAGE_SIZE,
            CLICK_PAGE_PREFETCH_DISTANCE,
            false,
            CLICK_PAGE_SIZE,
            CLICK_MAX_PAGE_SIZE
        )
    }

}