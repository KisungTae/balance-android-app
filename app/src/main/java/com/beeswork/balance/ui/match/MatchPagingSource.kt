package com.beeswork.balance.ui.match

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.repository.match.MatchRepository

class MatchPagingSource(
    private val matchRepository: MatchRepository,
    private val searchKeyword: String
) : PagingSource<Int, Match>() {

    override fun getRefreshKey(state: PagingState<Int, Match>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Match> {
        val currentPage = params.key ?: 0
        val matches = loadMatches(params.loadSize, (currentPage * params.loadSize))
        val prevPage = if (currentPage >= 1) currentPage - 1 else null
        val nextPage = if (matches.isEmpty()) null else currentPage + 1
        return LoadResult.Page(matches, prevPage, nextPage)
    }

    private suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match> {
        return if (searchKeyword.isEmpty()) matchRepository.loadMatches(loadSize, startPosition)
        else matchRepository.loadMatches(loadSize, startPosition, searchKeyword)
    }
}