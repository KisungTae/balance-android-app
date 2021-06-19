package com.beeswork.balance.internal.mapper.swipe

import com.beeswork.balance.data.database.entity.SwipeFilter
import com.beeswork.balance.ui.swipe.filter.SwipeFilterDomain

class SwipeFilterMapperImpl : SwipeFilterMapper {
    override fun toSwipeFilterDomain(swipeFilter: SwipeFilter): SwipeFilterDomain {
        return SwipeFilterDomain(swipeFilter.gender, swipeFilter.minAge, swipeFilter.maxAge, swipeFilter.distance)
    }
}