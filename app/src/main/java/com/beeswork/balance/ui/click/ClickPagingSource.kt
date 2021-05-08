package com.beeswork.balance.ui.click

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.repository.click.ClickRepository

class ClickPagingSource(
    private val clickRepository: ClickRepository
): PagingSource<Int, Click>() {
    override fun getRefreshKey(state: PagingState<Int, Click>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Click> {
        val currentPage = params.key ?: 0
        val matches = clickRepository.loadClicks(params.loadSize, (currentPage * params.loadSize))
        val prevPage = if (currentPage >= 1) currentPage - 1 else null
        val nextPage = if (matches.isEmpty()) null else currentPage + 1
        return LoadResult.Page(matches, prevPage, nextPage)
    }

}