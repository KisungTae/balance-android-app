package com.beeswork.balance.internal.mapper.swipe

import com.beeswork.balance.data.database.entity.swipe.SwipeFilter
import com.beeswork.balance.ui.swipe.filter.SwipeFilterDomain

interface SwipeFilterMapper {
    fun toSwipeFilterDomain(swipeFilter: SwipeFilter): SwipeFilterDomain
}