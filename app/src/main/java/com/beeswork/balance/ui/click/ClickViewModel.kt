package com.beeswork.balance.ui.click

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.util.lazyDeferred

const val CLICK_PAGE_SIZE = 15
const val CLICK_PAGE_PREFETCH_DISTANCE = CLICK_PAGE_SIZE * 2
const val CLICK_MAX_PAGE_SIZE = CLICK_PAGE_PREFETCH_DISTANCE * 2 + CLICK_PAGE_SIZE

class ClickViewModel(
    private val balanceRepository: BalanceRepository
): ViewModel() {

    val fetchClickListResponse: LiveData<Resource<List<Click>>> = balanceRepository.fetchClickListResponse

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setMaxSize(CLICK_MAX_PAGE_SIZE)
        .setInitialLoadSizeHint(CLICK_PAGE_SIZE)
        .setPageSize(CLICK_PAGE_SIZE)
//        .setPrefetchDistance(CLICK_PAGE_PREFETCH_DISTANCE)
        .build()

    val clicks by lazyDeferred {
        LivePagedListBuilder(balanceRepository.getClickedList(), pagedListConfig).build()
    }

    fun fetchClicks() {
        balanceRepository.fetchClickedList()
    }

    fun swipe(swipeId: String) {
        balanceRepository.swipe(null, swipeId)
    }

}