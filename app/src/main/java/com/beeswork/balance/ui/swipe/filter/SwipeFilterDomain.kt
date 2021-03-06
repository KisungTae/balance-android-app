package com.beeswork.balance.ui.swipe.filter

import com.beeswork.balance.data.database.entity.SwipeFilter
import com.beeswork.balance.internal.constant.Gender

data class SwipeFilterDomain(
    val gender: Boolean,
    val minAge: Int,
    val maxAge: Int,
    val distance: Int
)