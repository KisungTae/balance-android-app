package com.beeswork.balance.ui.matchfragment

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.constant.MatchPageFilter
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class MatchViewModel(
    private val matchRepository: MatchRepository,
    private val preferenceProvider: PreferenceProvider,
    private val matchMapper: MatchMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val matchPageInvalidationLiveData by viewModelLazyDeferred {
        matchRepository.getMatchPageInvalidationFlow().asLiveData()
    }

    @ExperimentalPagingApi
    fun initMatchPagingData(matchPageFilter: MatchPageFilter?): LiveData<PagingData<MatchItemUIState>> {
        return Pager(
            config = matchPagingConfig,
            remoteMediator = MatchRemoteMediator(matchRepository, matchPageFilter)
        ) {
            MatchPagingSource(matchRepository, matchPageFilter)
        }.flow.cachedIn(viewModelScope)
            .map { pagingData ->
                pagingData.map { match ->
                    matchMapper.toItemUIState(match, preferenceProvider.getPhotoBucketUrl())
                }
            }
            .asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    fun test() {
        viewModelScope.launch {
            matchRepository.testFunction()
        }
    }

    companion object {
        private const val MATCH_PAGE_SIZE = 30
        private const val MATCH_PAGE_PREFETCH_DISTANCE = MATCH_PAGE_SIZE
        private const val MATCH_MAX_PAGE_SIZE = MATCH_PAGE_PREFETCH_DISTANCE * 3 + MATCH_PAGE_SIZE
        private val matchPagingConfig = PagingConfig(
            MATCH_PAGE_SIZE,
            MATCH_PAGE_PREFETCH_DISTANCE,
            false,
            MATCH_PAGE_SIZE,
            MATCH_MAX_PAGE_SIZE
        )
    }
}

