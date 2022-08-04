package com.beeswork.balance.ui.swipefragment

import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.domain.uistate.swipe.SwipeUIState
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import com.beeswork.balance.ui.common.paging.LoadParam
import com.beeswork.balance.ui.common.paging.LoadResult
import com.beeswork.balance.ui.common.paging.LoadType
import com.beeswork.balance.ui.common.paging.PagingSource
import java.io.IOException

class SwipePagingSource(
    private val swipeRepository: SwipeRepository,
    private val swipeMapper: SwipeMapper
) : PagingSource<Long, SwipeUIState>() {

    override suspend fun load(loadParam: LoadParam<Long>): LoadResult<Long, SwipeUIState> {
        return try {
            if (loadParam.loadType == LoadType.REFRESH_DATA) {
                swipeRepository.deleteSwipes()
            }
            val response = if (loadParam.loadType == LoadType.REFRESH_PAGE) {
                swipeRepository.refreshSwipePage(loadParam.loadKey, loadParam.loadSize)
            } else {
                swipeRepository.fetchSwipePage(
                    loadParam.loadKey,
                    loadParam.loadSize,
                    loadParam.loadType.isAppend(),
                    loadParam.loadType.isIncludeLoadKey()
                )
            }
            return if (response.isSuccess() && response.data != null) {
                val swipes = response.data.map { swipe ->
                    swipeMapper.toSwipeUIStateItem(swipe)
                }
                LoadResult.Success(swipes, loadParam)
            } else {
                LoadResult.Error(loadParam.loadType, null)
            }
        } catch (e: IOException) {
            LoadResult.Error(loadParam.loadType, e)
        }
    }

}