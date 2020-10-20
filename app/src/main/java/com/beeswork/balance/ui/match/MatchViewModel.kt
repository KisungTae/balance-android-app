package com.beeswork.balance.ui.match

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.entity.Match
import com.beeswork.balance.data.repository.BalanceRepository
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.lazyDeferred

class MatchViewModel (
    private val balanceRepository: BalanceRepository
): ViewModel() {

    val fetchMatchesResource: LiveData<Resource<List<Match>>> = balanceRepository.fetchMatchesResource

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setMaxSize(MATCH_MAX_PAGE_SIZE)
        .setInitialLoadSizeHint(MATCH_PAGE_SIZE)
        .setPageSize(MATCH_PAGE_SIZE)
        .setPrefetchDistance(MATCH_PAGE_PREFETCH_DISTANCE)
        .build()

    val matches by lazyDeferred {
        LivePagedListBuilder(balanceRepository.getMatches(), pagedListConfig).build()
    }

    fun fetchMatches() {
        balanceRepository.fetchMatches()
    }


}