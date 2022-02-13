package com.beeswork.balance.internal.mapper.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.ui.swipefragment.SwipeDomain

interface SwipeMapper {
    fun toSwipe(swipeDTO: SwipeDTO): Swipe?
    fun toSwipeDomain(swipe: Swipe): SwipeDomain
}