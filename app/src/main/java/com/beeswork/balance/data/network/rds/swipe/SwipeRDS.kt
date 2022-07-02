package com.beeswork.balance.data.network.rds.swipe

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.ListSwipesDTO

interface SwipeRDS {
    suspend fun listSwipes(loadSize: Int, startPosition: Int): Resource<ListSwipesDTO>
    suspend fun fetchSwipes(loadKey: Long?, loadSize: Int, append: Boolean): Resource<ListSwipesDTO>
}