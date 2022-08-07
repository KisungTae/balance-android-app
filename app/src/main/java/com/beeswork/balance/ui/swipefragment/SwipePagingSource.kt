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
            swipeRepository.deleteSwipes(loadParam.loadKey, loadParam.loadType.isAppend())
            val response = swipeRepository.loadSwipes(loadParam).map { swipes ->
                swipes?.map { swipe ->
                    swipeMapper.toSwipeUIStateItem(swipe)
                }
            }

            return if (response.isSuccess() && response.data != null) {
                LoadResult.Success(response.data, loadParam)
            } else {
                LoadResult.Error(loadParam.loadType, response.exception)
            }
        } catch (e: IOException) {
            LoadResult.Error(loadParam.loadType, e)
        }
    }

}