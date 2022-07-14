package com.beeswork.balance.ui.swipefragment

import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.domain.uistate.swipe.SwipeUIState
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import com.beeswork.balance.ui.common.paging.LoadResult
import com.beeswork.balance.ui.common.paging.LoadType
import com.beeswork.balance.ui.common.paging.PagingSource
import java.io.IOException

class SwipePagingSource(
    private val swipeRepository: SwipeRepository,
    private val swipeMapper: SwipeMapper
) : PagingSource<Long, SwipeUIState>() {

    override suspend fun load(loadKey: Long?, loadType: LoadType, loadSize: Int): LoadResult<Long, SwipeUIState> {
        return try {
            if (loadType == LoadType.INITIAL_LOAD) {
                swipeRepository.deleteSwipes()
            }
            val response = swipeRepository.loadSwipes(loadKey, loadSize, loadType.isAppend(), loadType.isIncludeLoadKey())
            return if (response.isSuccess() && response.data != null) {
                val swipes = response.data.map { swipe ->
                    swipeMapper.toSwipeUIStateItem(swipe)
                }
                LoadResult.Success(swipes, loadType, swipes.first().key, swipes.last().key)
            } else {
                LoadResult.Error(loadType, null)
            }
        } catch (e: IOException) {
            LoadResult.Error(loadType, e)
        }
    }

//    private val pagingKeyTracker = PagingKeyTracker<Swipe>()
//
//    override fun getRefreshKey(state: PagingState<Int, Swipe>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            val loadSize = pagingKeyTracker.addRefreshedPageKeys(anchorPage) * state.config.pageSize
//            val startPosition = pagingKeyTracker.prevKey?.times(state.config.pageSize)
//            swipeRepository.syncSwipes(loadSize, startPosition)
//            return pagingKeyTracker.currKey
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Swipe> {
//        return try {
//            if (params is LoadParams.Refresh) {
//                println("params is LoadParams.Refresh")
//            } else if (params is LoadParams.Prepend) {
//                println("params is LoadParams.Prepend")
//            } else if (params is LoadParams.Append) {
//                println("params is LoadParams.Append")
//            }
//
//
//            val currentPage = params.key ?: 0
//            println("currentPage: $currentPage")
//            val startPosition = currentPage * params.loadSize
//            val swipes = swipeRepository.loadSwipes(params.loadSize, startPosition, pagingKeyTracker.shouldSyncPage(currentPage))
//
//            val prevPage = if (currentPage >= 1) {
//                currentPage - 1
//            } else {
//                null
//            }
//            val nextPage = if (swipes.isEmpty()) {
//                null
//            } else {
//                currentPage + 1
//            }
//            LoadResult.Page(swipes, prevPage, nextPage)
//        } catch (exception: IOException) {
//            LoadResult.Error(exception)
//        }
//
//    }

}