package com.beeswork.balance.data.network.rds.swipe

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.data.network.response.swipe.CountSwipesDTO
import java.util.*

interface SwipeRDS {
    suspend fun listSwipes(loadSize: Int, startPosition: Int): Resource<List<SwipeDTO>>
    suspend fun fetchSwipes(loadSize: Int, lastSwiperId: UUID?): Resource<List<SwipeDTO>>
    suspend fun countSwipes(): Resource<CountSwipesDTO>
}