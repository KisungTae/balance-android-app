package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import kotlinx.coroutines.flow.Flow

interface SwipeRepository {

    val newSwipeFlow: Flow<Swipe>

    suspend fun loadSwipes(loadKey: Long?, loadSize: Int, isAppend: Boolean, isIncludeLoadKey: Boolean): Resource<List<Swipe>>

    suspend fun saveSwipe(swipeDTO: SwipeDTO)
    suspend fun deleteSwipes()
    fun getSwipePageInvalidationFlow(): Flow<Boolean>
    fun syncSwipes(loadSize: Int, startPosition: Int?)

    suspend fun test()
}