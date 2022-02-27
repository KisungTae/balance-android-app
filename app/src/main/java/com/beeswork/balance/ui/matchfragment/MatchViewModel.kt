package com.beeswork.balance.ui.matchfragment

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.MatchPageFilter
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class MatchViewModel(
    private val matchRepository: MatchRepository,
    private val chatRepository: ChatRepository,
    private val matchMapper: MatchMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val matchPageInvalidationLiveData by viewModelLazyDeferred {
        matchRepository.getMatchPageInvalidationFlow().asLiveData()
    }

    @ExperimentalPagingApi
    fun initMatchPagingData(matchPageFilter: MatchPageFilter?): LiveData<PagingData<MatchDomain>> {
        return Pager(
            config = matchPagingConfig,
            remoteMediator = MatchRemoteMediator(matchRepository, matchPageFilter)
        ) {
            MatchPagingSource(matchRepository, matchPageFilter)
        }.flow.cachedIn(viewModelScope)
            .map { pagingData ->
                pagingData.map { matchMapper.toMatchDomain(it) }
            }
            .asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    fun testFunction() {
        viewModelScope.launch {
            matchRepository.testFunction()


//            matchRepository.saveMatch(
//                MatchDTO(
//                    PushType.MATCHED,
//                    1,
//                    UUID.fromString("2c2743bf-23ab-4e23-bd4e-4955b8191e12"),
//                    UUID.randomUUID(),
//                    false,
//                    false,
//                    "Michael",
//                    "key",
//                    OffsetDateTime.now()
//                )
//            )
//            chatRepository.sendChatMessage(1, UUID.randomUUID(), "test")
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

