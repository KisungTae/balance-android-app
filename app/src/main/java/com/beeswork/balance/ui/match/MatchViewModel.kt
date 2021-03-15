package com.beeswork.balance.ui.match

import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.util.lazyDeferred
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.OffsetDateTime


class MatchViewModel(
    private val matchRepository: MatchRepository,
    private val matchMapper: MatchMapper
) : ViewModel() {

    private val _fetchMatchesLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val fetchMatchesLiveData: LiveData<Resource<EmptyResponse>> get() = _fetchMatchesLiveData

    private val matchSearchKeywordChannel = ConflatedBroadcastChannel<String>()
    private var matchDataSource: MatchDataSource? = null
    val matchPagedListLiveData by lazyDeferred { initializeMatchPagedListLiveData() }

    init {
        matchSearchKeywordChannel.asFlow()
            .debounce(QUERY_DEBOUNCE)
            .onEach { matchDataSource?.invalidate() }
            .launchIn(viewModelScope)
    }

    private fun initializeMatchPagedListLiveData(): LiveData<PagedList<MatchDomain>> {
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setMaxSize(MATCH_MAX_PAGE_SIZE)
            .setInitialLoadSizeHint(MATCH_PAGE_SIZE)
            .setPageSize(MATCH_PAGE_SIZE)
            .setPrefetchDistance(MATCH_PREFETCH_DISTANCE)
            .build()

        val dataSource = object : DataSource.Factory<Int, Match>() {
            override fun create(): DataSource<Int, Match> {
                return MatchDataSource(
                    matchRepository,
                    viewModelScope,
                    matchSearchKeywordChannel.valueOrNull.orEmpty()
                ).also { matchDataSource = it }
            }
        }

        return LivePagedListBuilder(dataSource.map { matchMapper.fromEntityToDomain(it) }, pagedListConfig).build()
    }

    fun changeMatchSearchKeyword(input: String) {
        val searchKeyword = if (input.isNotEmpty()) "%$input%" else input
        matchSearchKeywordChannel.offer(searchKeyword)

    }

    fun fetchMatches() {
        CoroutineScope(Dispatchers.IO).launch {
            _fetchMatchesLiveData.postValue(matchRepository.fetchMatches())
        }
    }


//  TODO: remove me
    val matches by lazyDeferred {
        LivePagedListBuilder(
            matchRepository.loadMatchesAsFactory().map {
                matchMapper.fromEntityToDomain(it)
            }, PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setMaxSize(MATCH_MAX_PAGE_SIZE)
                .setInitialLoadSizeHint(MATCH_PAGE_SIZE)
                .setPageSize(MATCH_PAGE_SIZE)
                .setPrefetchDistance(MATCH_PREFETCH_DISTANCE)
                .build()
        ).build()
    }

    fun testFunction() {
        matchRepository.testFunction()
    }

    fun loadMoreMatches(pageSize: Int, lastUpdatedAt: OffsetDateTime) {

    }

    fun prependMatches(pageSize: Int, headUpdatedAt: OffsetDateTime): List<MatchDomain> {
        CoroutineScope(Dispatchers.IO).launch {
            return matchRepository.prependMatches(pageSize, headUpdatedAt).map { matchMapper.fromEntityToDomain(it) }
        }
    }

    fun appendMatches(pageSize: Int, tailUpdatedAt: OffsetDateTime): List<MatchDomain> {

    }

    companion object {
        private const val MATCH_PAGE_SIZE = 100
        private const val MATCH_PREFETCH_DISTANCE = MATCH_PAGE_SIZE
        private const val MATCH_MAX_PAGE_SIZE = MATCH_PREFETCH_DISTANCE * 3 + MATCH_PAGE_SIZE
        private const val QUERY_DEBOUNCE = 500L
    }


//    val matches by lazyDeferred {
//        LivePagedListBuilder(balanceRepository.getMatches().map {
//            matchMapper.fromEntityToDomain(it)
//        }, pagedListConfig).build()
//    }

}


// TODO: when back button to match page, should keep the position