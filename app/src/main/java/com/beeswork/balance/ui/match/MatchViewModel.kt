package com.beeswork.balance.ui.match

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.util.lazyDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MatchViewModel(
    private val balanceRepository: BalanceRepository,
    private val matchRepository: MatchRepository,
    private val matchMapper: MatchMapper
) : ViewModel() {

    private val _fetchMatches = MutableLiveData<Resource<EmptyResponse>>()
    val fetchMatches: LiveData<Resource<EmptyResponse>> get() = _fetchMatches

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setMaxSize(MATCH_MAX_PAGE_SIZE)
        .setInitialLoadSizeHint(MATCH_PAGE_SIZE)
        .setPageSize(MATCH_PAGE_SIZE)
        .setPrefetchDistance(MATCH_PREFETCH_DISTANCE)
        .build()

    val matches by lazyDeferred {
        LivePagedListBuilder(balanceRepository.getMatches().map {
            matchMapper.fromEntityToDomain(it)
        }, pagedListConfig).build()
    }

    fun fetchMatches() {
        CoroutineScope(Dispatchers.IO).launch {
            _fetchMatches.postValue(matchRepository.fetchMatches())
        }
    }

    companion object {
        private const val MATCH_PAGE_SIZE = 30
        private const val MATCH_PREFETCH_DISTANCE = 0
        private const val MATCH_MAX_PAGE_SIZE = MATCH_PAGE_SIZE * 4 + MATCH_PAGE_SIZE
    }


}