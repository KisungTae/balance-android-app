package com.beeswork.balance.ui.match

import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.PositionalDataSource
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.repository.match.MatchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MatchPagingSource(
    private val matchRepository: MatchRepository,
    private val scope: CoroutineScope,
    private val searchKeyword: String
) : PagingSource<Int, Match>() {

    override fun getRefreshKey(state: PagingState<Int, Match>): Int? {
        println("getRefreshKey")
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Match> {
        println("load - params.key: ${params.key}")

        val currentPage = params.key ?: 0
        val matches = loadMatches(params.loadSize, (currentPage * params.loadSize), searchKeyword)

        val prevPage = if (currentPage >= 1) currentPage - 1 else null
        val nextPage = if (matches.isEmpty()) null else currentPage + 1

        return LoadResult.Page(
            loadMatches(params.loadSize, (currentPage * params.loadSize), searchKeyword),
            prevPage,
            nextPage
        )
    }


//    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Match>) {
//        scope.launch {
//            println("loadInitial - loadSize: ${params.requestedLoadSize} - startPosition: ${params.requestedStartPosition}")
//
//            callback.onResult(
//                loadMatches(params.requestedLoadSize, params.requestedStartPosition, searchKeyword),
//                params.requestedStartPosition
//            )
//        }
//    }
//
//    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Match>) {
//        println("loadRange - loadSize: ${params.loadSize} - startPosition: ${params.startPosition}")
//
//        scope.launch {
//            callback.onResult(loadMatches(params.loadSize, params.startPosition, searchKeyword))
//        }
//    }
//
//
    private suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match> {
        return if (searchKeyword.isEmpty()) matchRepository.loadMatches(loadSize, startPosition)
        else matchRepository.loadMatches(loadSize, startPosition, searchKeyword)
    }
}