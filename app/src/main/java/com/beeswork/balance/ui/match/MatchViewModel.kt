package com.beeswork.balance.ui.match

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.internal.util.safeLaunch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime


class MatchViewModel(
    private val matchRepository: MatchRepository,
    private val chatRepository: ChatRepository,
    private val matchMapper: MatchMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    val matchInvalidation by lazyDeferred {
        matchRepository.getMatchInvalidation().asLiveData()
    }

    val newMatchLiveData by lazyDeferred {
        matchRepository.newMatchFlow.asLiveData()
    }

    private val _fetchMatchesLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val fetchMatchesLiveData: LiveData<Resource<EmptyResponse>> get() = _fetchMatchesLiveData

    private var fetchingMatches = false
    private var fetchingChatMessages = false

    fun initMatchPagingData(searchKeyword: String): LiveData<PagingData<MatchDomain>> {
        return Pager(
            pagingConfig,
            null,
            { MatchPagingSource(matchRepository, searchKeyword) }
        ).flow.cachedIn(viewModelScope)
            .map { pagingData -> pagingData.map { matchMapper.toMatchDomain(it) } }
            .asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    fun fetchMatches() {
        viewModelScope.launch {
            if (fetchingMatches) return@launch

            fetchingMatches = true
            _fetchMatchesLiveData.postValue(Resource.loading())
            val result = async { matchRepository.fetchMatches() }
            _fetchMatchesLiveData.postValue(result.await())

//            val response = matchRepository.fetchMatches()
//            _fetchMatchesLiveData.postValue(response)

            chatRepository.fetchChatMessages()
            println("after fetch matches()!!!!!!!!!!!!!!!!!!!!!!!")
            fetchingMatches = false
        }
    }

    fun fetchChatMessages() {
        viewModelScope.launch {
            if (fetchingChatMessages) return@launch

            fetchingChatMessages = true
            chatRepository.fetchChatMessages()
            println("after fetch chat messages() !!!!!!!!!!!!!!!!!!!!!!!")
            fetchingChatMessages = false
        }
    }

    fun testFunction() {
        matchRepository.testFunction()
    }

    companion object {
        private const val MATCH_PAGE_SIZE = 80
        private const val MATCH_PAGE_PREFETCH_DISTANCE = MATCH_PAGE_SIZE
        private const val MATCH_MAX_PAGE_SIZE = MATCH_PAGE_PREFETCH_DISTANCE * 3 + MATCH_PAGE_SIZE
        private val pagingConfig = PagingConfig(
            MATCH_PAGE_SIZE,
            MATCH_PAGE_PREFETCH_DISTANCE,
            false,
            MATCH_PAGE_SIZE,
            MATCH_MAX_PAGE_SIZE
        )
    }
}

