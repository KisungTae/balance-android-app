package com.beeswork.balance.ui.match

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.lazyDeferred


const val MATCH_PAGE_SIZE = 30
const val MATCH_PAGE_PREFETCH_DISTANCE = MATCH_PAGE_SIZE * 2
const val MATCH_MAX_PAGE_SIZE = MATCH_PAGE_PREFETCH_DISTANCE * 2 + MATCH_PAGE_SIZE

class MatchViewModel (
    private val balanceRepository: BalanceRepository
): ViewModel() {

    val fetchMatchesResponse: LiveData<Resource<List<Match>>> = balanceRepository.fetchMatchesResponse

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setMaxSize(MATCH_MAX_PAGE_SIZE)
        .setInitialLoadSizeHint(MATCH_PAGE_SIZE)
        .setPageSize(MATCH_PAGE_SIZE)
//        .setPrefetchDistance(MATCH_PAGE_PREFETCH_DISTANCE)
        .build()

    val matches by lazyDeferred {
        LivePagedListBuilder(balanceRepository.getMatches(), pagedListConfig).build()
    }

    fun fetchMatches() {
        balanceRepository.fetchMatches()
    }


}