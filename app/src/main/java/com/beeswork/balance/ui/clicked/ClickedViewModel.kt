package com.beeswork.balance.ui.clicked

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.lazyDeferred

const val CLICKED_PAGE_SIZE = 15
const val CLICKED_PAGE_PREFETCH_DISTANCE = CLICKED_PAGE_SIZE * 2
const val CLICKED_MAX_PAGE_SIZE = CLICKED_PAGE_PREFETCH_DISTANCE * 2 + CLICKED_PAGE_SIZE

class ClickedViewModel(
    private val balanceRepository: BalanceRepository
): ViewModel() {

    val fetchClickedListResponse: LiveData<Resource<List<Clicked>>> = balanceRepository.fetchClickedListResponse

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setMaxSize(CLICKED_MAX_PAGE_SIZE)
        .setInitialLoadSizeHint(CLICKED_PAGE_SIZE)
        .setPageSize(CLICKED_PAGE_SIZE)
        .setPrefetchDistance(CLICKED_PAGE_PREFETCH_DISTANCE)
        .build()

    val clickedList by lazyDeferred {
        LivePagedListBuilder(balanceRepository.getClickedList(), pagedListConfig).build()
    }

    fun fetchClickedList() {
        balanceRepository.fetchClickedList()
    }

    fun swipe(swipeId: String) {
        balanceRepository.swipe(null, swipeId)
    }

}