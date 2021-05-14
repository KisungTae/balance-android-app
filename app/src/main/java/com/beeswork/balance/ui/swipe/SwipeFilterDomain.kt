package com.beeswork.balance.ui.swipe

import com.beeswork.balance.data.database.entity.SwipeFilter
import com.beeswork.balance.internal.constant.Gender

data class SwipeFilterDomain(
    val gender: Gender,
    val minAge: Int,
    val maxAge: Int,
    val distance: Int
)