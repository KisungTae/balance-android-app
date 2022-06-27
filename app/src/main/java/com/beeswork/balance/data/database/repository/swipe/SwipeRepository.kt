package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.data.network.response.swipe.ListSwipesDTO
import com.beeswork.balance.ui.common.paging.LoadType
import kotlinx.coroutines.flow.Flow

interface SwipeRepository {

    val newSwipeFlow: Flow<Swipe>

    suspend fun loadSwipes(key: Long?, loadType: LoadType, loadSize: Int): Resource<List<Swipe>>

    suspend fun fetchSwipes(loadSize: Int, lastSwipeId: Long?): Resource<ListSwipesDTO>
    suspend fun loadSwipes(loadSize: Int, startPosition: Int, sync: Boolean): List<Swipe>
    suspend fun saveSwipe(swipeDTO: SwipeDTO)
    suspend fun deleteSwipes()
    fun getSwipePageInvalidationFlow(): Flow<Boolean>
    fun syncSwipes(loadSize: Int, startPosition: Int?)

    suspend fun test()
}