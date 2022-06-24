package com.beeswork.balance.ui.swipefragment

import com.beeswork.balance.domain.uistate.swipe.SwipeItemUIState
import com.beeswork.balance.ui.common.paging.Pager
import kotlinx.coroutines.CoroutineScope

class SwipePager(
    private val pageSize: Int,
    private val coroutineScope: CoroutineScope,
): Pager<SwipeItemUIState>(pageSize, coroutineScope) {
}