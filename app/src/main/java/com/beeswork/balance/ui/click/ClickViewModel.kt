package com.beeswork.balance.ui.click

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.click.ClickMapper
import com.beeswork.balance.internal.util.safeLaunch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ClickViewModel(
    private val clickRepository: ClickRepository,
    private val clickMapper: ClickMapper
) : ViewModel() {

//    val fetchClickListResponse: LiveData<Resource<List<Click>>> = balanceRepository.fetchClickListResponse
//    val clicks by lazyDeferred {
//        LivePagedListBuilder(balanceRepository.getClickedList(), pagedListConfig).build()
//    }

    val newClickLiveData = clickRepository.newClickFlow.map { clickMapper.fromEntityToDomain(it) }.asLiveData()

    private val _fetchClicks = MutableLiveData<Resource<EmptyResponse>>()
    val fetchClicks: LiveData<Resource<EmptyResponse>> get() = _fetchClicks

    fun initInvalidation(): LiveData<Boolean> {
        return clickRepository.getInvalidation().asLiveData()
    }

    fun initClickPagingData(): LiveData<PagingData<ClickDomain>> {
        return Pager(
            pagingConfig,
            null,
            { ClickPagingSource(clickRepository) }
        ).flow.cachedIn(viewModelScope)
            .map { pagingData ->
                pagingData.map { clickMapper.fromEntityToDomain(it) }
            }.map { pagingData ->
                pagingData.insertHeaderItem(TerminalSeparatorType.FULLY_COMPLETE, ClickDomain.header())
            }.asLiveData(viewModelScope.coroutineContext)
    }

    fun fetchClicks() {
        viewModelScope.safeLaunch(_fetchClicks) {
            _fetchClicks.postValue(clickRepository.fetchClicks())
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
        private const val CLICK_PAGE_SIZE = 80
        private const val CLICK_PAGE_PREFETCH_DISTANCE = CLICK_PAGE_SIZE * 2
        private const val CLICK_MAX_PAGE_SIZE = CLICK_PAGE_PREFETCH_DISTANCE * 2 + CLICK_PAGE_SIZE
        private val pagingConfig = PagingConfig(
            CLICK_PAGE_SIZE,
            CLICK_PAGE_PREFETCH_DISTANCE,
            false,
            CLICK_PAGE_SIZE,
            CLICK_MAX_PAGE_SIZE
        )
    }

}