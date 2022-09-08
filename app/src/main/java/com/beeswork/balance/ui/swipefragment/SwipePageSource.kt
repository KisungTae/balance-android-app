package com.beeswork.balance.ui.swipefragment

import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.domain.uistate.swipe.SwipeUIState
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import com.beeswork.balance.ui.common.page.PageLoadParam
import com.beeswork.balance.ui.common.page.PageLoadResult
import com.beeswork.balance.ui.common.page.PageSource
import java.io.IOException

class SwipePageSource(
    private val swipeRepository: SwipeRepository,
    private val swipeMapper: SwipeMapper
) : PageSource<Long, SwipeUIState>() {

    override suspend fun load(pageLoadParam: PageLoadParam<Long>): PageLoadResult<Long, SwipeUIState> {
        return try {
            swipeRepository.deleteSwipes(pageLoadParam.loadKey, pageLoadParam.pageLoadType.isAppend())
            val response = swipeRepository.loadSwipes(pageLoadParam).map { swipes ->
                swipes?.map { swipe ->
                    swipeMapper.toSwipeUIStateItem(swipe)
                }
            }

            return if (response.isSuccess() && response.data != null) {
                PageLoadResult.Success(response.data, pageLoadParam)
            } else {
                PageLoadResult.Error(pageLoadParam.pageLoadType, response.exception)
            }
        } catch (e: IOException) {
            PageLoadResult.Error(pageLoadParam.pageLoadType, e)
        }
    }

}