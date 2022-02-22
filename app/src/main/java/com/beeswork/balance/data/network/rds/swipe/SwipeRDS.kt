package com.beeswork.balance.data.network.rds.swipe

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.data.network.response.swipe.CountSwipesDTO
import com.beeswork.balance.data.network.response.swipe.ListSwipesDTO
import java.util.*

interface SwipeRDS {
    suspend fun listSwipes(loadSize: Int, startPosition: Int): Resource<ListSwipesDTO>
    suspend fun fetchSwipes(loadSize: Int, lastSwiperId: UUID?): Resource<ListSwipesDTO>
}