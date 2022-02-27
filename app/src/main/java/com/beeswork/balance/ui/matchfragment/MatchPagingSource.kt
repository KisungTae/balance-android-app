package com.beeswork.balance.ui.matchfragment

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.constant.MatchPageFilter
import java.io.IOException

class MatchPagingSource(
    private val matchRepository: MatchRepository,
    private val matchPageFilter: MatchPageFilter?
) : PagingSource<Int, Match>() {

    override fun getRefreshKey(state: PagingState<Int, Match>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Match> {
        return try {
            val currentPage = params.key ?: 0
            val startPosition = currentPage * params.loadSize
            val matches = matchRepository.loadMatches(params.loadSize, startPosition, matchPageFilter)
            val prevPage = if (currentPage >= 1) currentPage - 1 else null
            val nextPage = if (matches.isEmpty()) null else currentPage + 1
            LoadResult.Page(matches, prevPage, nextPage)
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        }

    }
}