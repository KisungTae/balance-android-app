package com.beeswork.balance.ui.click

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.database.repository.click.ClickRepository
import java.io.IOException
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.RuntimeException
import kotlin.random.Random

class ClickPagingSource(
    private val clickRepository: ClickRepository
) : PagingSource<Int, Click>() {

    override fun getRefreshKey(state: PagingState<Int, Click>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Click> {
        return try {
            val currentPage = params.key ?: 0
            val startPosition = currentPage * params.loadSize
            val response = clickRepository.loadClicks(params.loadSize, startPosition)


            if (response.isError()) {
                if (response.exception == null) {
                    LoadResult.Error<Int, Click>(RuntimeException())
                } else {
                    LoadResult.Error<Int, Click>(response.exception)
                }
            }

            val clicks = response.data ?: listOf()
            val prevPage = if (currentPage >= 1) currentPage - 1 else null
            val nextPage = if (clicks.isEmpty()) null else currentPage + 1
            LoadResult.Page(clicks, prevPage, nextPage)
        } catch (e: IOException) {
            LoadResult.Error(e)
        }

    }

}