package com.beeswork.balance.ui.swipefragment

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import java.io.IOException

class SwipePagingSource(
    private val swipeRepository: SwipeRepository
) : PagingSource<Int, Swipe>() {

    override fun getRefreshKey(state: PagingState<Int, Swipe>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Swipe> {
        return try {
            val currentPage = params.key ?: 0
            val startPosition = currentPage * params.loadSize
            val swipes = swipeRepository.loadSwipes(params.loadSize, startPosition)
            val prevPage = if (currentPage >= 1) currentPage - 1 else null
            val nextPage = if (swipes.isEmpty()) null else currentPage + 1
            LoadResult.Page(swipes, prevPage, nextPage)
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        }

    }

}