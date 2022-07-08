package com.beeswork.balance.data.network.rds.swipe

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.ListSwipesDTO
import com.beeswork.balance.data.network.response.swipe.SwipeDTO

interface SwipeRDS {
    suspend fun listSwipes(loadSize: Int, startPosition: Int): Resource<ListSwipesDTO>
    suspend fun fetchSwipes(loadKey: Long?, loadSize: Int, isAppend: Boolean, isIncludeLoadKey: Boolean): Resource<List<SwipeDTO>>
}