package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.entity.SwipeFilter
import com.beeswork.balance.internal.constant.Gender

interface SwipeRepository {

    suspend fun getSwipeFilter(): SwipeFilter
    suspend fun saveSwipeFilter(gender: Gender, minAge: Int, maxAge: Int, distance: Int)

}