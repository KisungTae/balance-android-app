package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.data.network.response.swipe.FetchSwipesDTO
import kotlinx.coroutines.flow.Flow
import java.util.*

interface SwipeRepository {

    val newSwipeFlow: Flow<Swipe>
    val swipeCountFlow: Flow<Long?>

    suspend fun fetchSwipes(loadSize: Int, lastSwiperId: UUID?): Resource<FetchSwipesDTO>
    suspend fun loadSwipes(loadSize: Int, startPosition: Int): List<Swipe>
    suspend fun saveSwipe(swipeDTO: SwipeDTO)
    suspend fun syncSwipeCount()
    suspend fun deleteSwipes()
    fun getSwipePageInvalidationFlow(): Flow<Boolean>

    fun test()
}