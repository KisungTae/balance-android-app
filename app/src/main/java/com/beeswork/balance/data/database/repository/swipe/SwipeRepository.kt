package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.entity.SwipeFilter

interface SwipeRepository {

    suspend fun getSwipeFilter(): SwipeFilter
}