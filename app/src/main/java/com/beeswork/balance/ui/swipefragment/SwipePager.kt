package com.beeswork.balance.ui.swipefragment

import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.domain.uistate.swipe.SwipeItemUIState
import com.beeswork.balance.ui.common.paging.LoadType
import com.beeswork.balance.ui.common.paging.Page
import com.beeswork.balance.ui.common.paging.Pager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class SwipePager(
    private val swipeRepository: SwipeRepository,
    pageSize: Int,
    numOfPagesToKeep: Int,
    coroutineScope: CoroutineScope,
) : Pager<SwipeItemUIState, Long>(pageSize, numOfPagesToKeep, coroutineScope) {

    override fun load(key: Long?, loadType: LoadType) {
        coroutineScope.launch {
            try {
                withContext(coroutineDispatcher) {
                    val response = swipeRepository.loadSwipes(key, loadType, pageSize)
                    if (response.isSuccess() && response.data != null) {
                        val swipeItemUIStates = response.data.map { swipe ->
//                            SwipeItemUIState(swipe.id, swipe.swiperId, swipe.clicked, null)
                        }
//                        val page = Page.Success(response.data, response.data.first().id, response.data.last().id, )
                    }
                    pageLiveData.postValue(Page.Error(loadType, response.exception))
                }
            } catch (e: IOException) {
                pageLiveData.postValue(Page.Error(loadType, e))
            }
        }
    }
}