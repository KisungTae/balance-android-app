package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.data.network.response.swipe.ListSwipesDTO
import kotlinx.coroutines.flow.Flow

interface SwipeRepository {

    val newSwipeFlow: Flow<Swipe>

    suspend fun fetchSwipes(loadSize: Int, lastSwipeId: Long?): Resource<ListSwipesDTO>
    suspend fun loadSwipes(loadSize: Int, startPosition: Int): List<Swipe>
    suspend fun saveSwipe(swipeDTO: SwipeDTO)
    suspend fun deleteSwipes()
    suspend fun deleteSwipeSwipeCount()
    fun getSwipePageInvalidationFlow(): Flow<Boolean>
    fun getSwipeCountFlow(): Flow<Long?>

    suspend fun test()
}