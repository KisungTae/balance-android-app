package com.beeswork.balance.ui.clicked

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.constant.MATCH_MAX_PAGE_SIZE
import com.beeswork.balance.internal.constant.MATCH_PAGE_PREFETCH_DISTANCE
import com.beeswork.balance.internal.constant.MATCH_PAGE_SIZE
import com.beeswork.balance.internal.lazyDeferred

class ClickedViewModel(
    private val balanceRepository: BalanceRepository
): ViewModel() {

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setMaxSize(MATCH_MAX_PAGE_SIZE)
        .setInitialLoadSizeHint(MATCH_PAGE_SIZE)
        .setPageSize(MATCH_PAGE_SIZE)
        .setPrefetchDistance(MATCH_PAGE_PREFETCH_DISTANCE)
        .build()

    val clicked by lazyDeferred {
        LivePagedListBuilder(balanceRepository.getClicked(), pagedListConfig).build()
    }

    fun fetchClicked() {
        balanceRepository.fetchClicked()
    }
}