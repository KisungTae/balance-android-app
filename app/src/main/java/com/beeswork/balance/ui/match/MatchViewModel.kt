package com.beeswork.balance.ui.match

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.util.safeLaunch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MatchViewModel(
    private val matchRepository: MatchRepository,
    private val matchMapper: MatchMapper,
) : ViewModel() {

    private val _fetchMatchesLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val fetchMatchesLiveData: LiveData<Resource<EmptyResponse>> get() = _fetchMatchesLiveData

    val matchPagingRefreshLiveData = matchRepository.matchPagingRefreshFlow.asLiveData()

    fun initMatchPagingData(searchKeyword: String): LiveData<PagingData<MatchDomain>> {
        return Pager(
            pagingConfig,
            null,
            { MatchPagingSource(matchRepository, searchKeyword) }
        ).flow.cachedIn(viewModelScope)
            .map { pagingData -> pagingData.map { matchMapper.fromEntityToDomain(it) } }
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun fetchMatches() {
        viewModelScope.safeLaunch(_fetchMatchesLiveData) {
            _fetchMatchesLiveData.postValue(matchRepository.fetchMatches())
        }
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

