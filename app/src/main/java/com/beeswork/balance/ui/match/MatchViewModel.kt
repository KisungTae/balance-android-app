package com.beeswork.balance.ui.match

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.match.MatchMapper
import kotlinx.coroutines.flow.*


class MatchViewModel(
    private val matchRepository: MatchRepository,
    private val matchMapper: MatchMapper
) : ViewModel() {

//    private val matchSearchKeywordChannel = ConflatedBroadcastChannel<String>()
//    private var matchDataSource: MatchDataSource = MatchDataSource(
//        matchRepository,
//        viewModelScope,
//        matchSearchKeywordChannel.valueOrNull.orEmpty()
//    )
//    val matchPagedListLiveData by lazyDeferred { initializeMatchPagedListLiveData() }


//    val matches = Pager(
//        PagingConfig(
//            MATCH_PAGE_SIZE, MATCH_PREFETCH_DISTANCE, false, MATCH_PAGE_SIZE, MATCH_MAX_PAGE_SIZE
//        )
//    ) {
//        matchDataSource
//    }.flow.cachedIn(viewModelScope).map { pagingData -> pagingData.map { matchMapper.fromEntityToDomain(it) } }

//    val matches = initializeMatches()

    init {
//        matchSearchKeywordChannel.asFlow()
//            .debounce(QUERY_DEBOUNCE)
//            .onEach { matchDataSource?.invalidate() }
//            .launchIn(viewModelScope)
    }

    private lateinit var matchPagingSource: MatchPagingSource

    fun initializeMatches(query: String): Flow<PagingData<MatchDomain>> {
        val pagingConfig = PagingConfig(
            MATCH_PAGE_SIZE,
            MATCH_PREFETCH_DISTANCE,
            false,
            MATCH_PAGE_SIZE,
            MATCH_MAX_PAGE_SIZE
        )

//        matchDataSource = MatchDataSource(matchRepository, viewModelScope, query)

        return Pager(
            pagingConfig,
            null,
            { MatchPagingSource(matchRepository, viewModelScope, query) }
        ).flow.cachedIn(viewModelScope).map { pagingData -> pagingData.map { matchMapper.fromEntityToDomain(it) } }

//        return Pager(pagingConfig) { matchDataSource }.flow.cachedIn(viewModelScope)
//            .map { pagingData -> pagingData.map { matchMapper.fromEntityToDomain(it) } }

//        matchDataSource?.let {
//            return Pager(pagingConfig) { it }.flow.cachedIn(viewModelScope)
//                .map { pagingData -> pagingData.map { matchMapper.fromEntityToDomain(it) } }
//        }
//        return null
    }

//    private fun initializeMatchPagedListLiveData(): LiveData<PagedList<MatchDomain>> {
//        val pagedListConfig = PagedList.Config.Builder()
//            .setEnablePlaceholders(false)
//            .setMaxSize(MATCH_MAX_PAGE_SIZE)
//            .setInitialLoadSizeHint(MATCH_PAGE_SIZE)
//            .setPageSize(MATCH_PAGE_SIZE)
//            .setPrefetchDistance(MATCH_PREFETCH_DISTANCE)
//            .build()
//
//        val dataSource = object : DataSource.Factory<Int, Match>() {
//            override fun create(): DataSource<Int, Match> {
//                return MatchDataSource(
//                    matchRepository,
//                    viewModelScope,
//                    matchSearchKeywordChannel.valueOrNull.orEmpty()
//                ).also { matchDataSource = it }
//            }
//        }
//
//        return LivePagedListBuilder(
//            dataSource.map { matchMapper.fromEntityToDomain(it) },
//            pagedListConfig
//        ).setInitialLoadKey(initialLoadKey).build()
//    }

    fun changeMatchSearchKeyword(input: String) {
//        val searchKeyword = if (input.isNotEmpty()) "%$input%" else input
//        matchSearchKeywordChannel.offer(searchKeyword)
    }

    fun fetchMatches() {

    }


    fun testFunction() {
        matchRepository.testFunction()
//        matchDataSource.invalidate()
    }

    companion object {
        private const val MATCH_PAGE_SIZE = 50
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