package com.beeswork.balance.ui.match

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.bumptech.glide.load.engine.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MatchViewModel(
    private val matchRepository: MatchRepository,
    private val matchMapper: MatchMapper
) : ViewModel() {

    val fetchMatchesLiveData = matchRepository.fetchMatchesLiveData

    fun initMatchPagingData(searchKeyword: String): Flow<PagingData<MatchDomain>> {
        return Pager(
            pagingConfig,
            null,
            { MatchPagingSource(matchRepository, searchKeyword) }
        ).flow.cachedIn(viewModelScope).map { pagingData -> pagingData.map { matchMapper.fromEntityToDomain(it) } }
    }

    fun fetchMatches() {
        viewModelScope.launch { matchRepository.fetchMatches() }
    }

    fun testFunction() {
        matchRepository.testFunction()
    }

    companion object {
        private const val MATCH_PAGE_SIZE = 80
        private const val MATCH_PREFETCH_DISTANCE = MATCH_PAGE_SIZE
        private const val MATCH_MAX_PAGE_SIZE = MATCH_PREFETCH_DISTANCE * 3 + MATCH_PAGE_SIZE
        private val pagingConfig = PagingConfig(
            MATCH_PAGE_SIZE,
            MATCH_PREFETCH_DISTANCE,
            false,
            MATCH_PAGE_SIZE,
            MATCH_MAX_PAGE_SIZE
        )
    }
}

