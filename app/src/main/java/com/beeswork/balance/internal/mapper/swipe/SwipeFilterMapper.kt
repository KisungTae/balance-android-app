package com.beeswork.balance.internal.mapper.swipe

import com.beeswork.balance.data.database.entity.SwipeFilter
import com.beeswork.balance.ui.swipe.SwipeFilterDomain

interface SwipeFilterMapper {
    fun toSwipeFilterDomain(swipeFilter: SwipeFilter): SwipeFilterDomain
}