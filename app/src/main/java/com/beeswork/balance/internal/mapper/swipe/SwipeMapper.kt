package com.beeswork.balance.internal.mapper.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.domain.uistate.swipe.SwipeNotificationUIState
import com.beeswork.balance.domain.uistate.swipe.SwipeItemUIState

interface SwipeMapper {
    fun toSwipe(swipeDTO: SwipeDTO): Swipe?
    fun toSwipeItemUIState(swipe: Swipe): SwipeItemUIState
    fun toSwipeNotificationUIState(swipe: Swipe): SwipeNotificationUIState
}