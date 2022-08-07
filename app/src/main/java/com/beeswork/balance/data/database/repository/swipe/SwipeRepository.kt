package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.ui.common.paging.LoadParam
import kotlinx.coroutines.flow.Flow

interface SwipeRepository {

    val newSwipeFlow: Flow<Swipe>

    suspend fun loadSwipes(loadParam: LoadParam<Long>): Resource<List<Swipe>>

    suspend fun saveSwipe(swipeDTO: SwipeDTO)
    suspend fun deleteSwipes()
    suspend fun deleteSwipes(loadKey: Long?, isAppend: Boolean)
    fun getSwipePageInvalidationFlow(): Flow<Boolean>
    fun syncSwipes(loadSize: Int, startPosition: Int?)

    suspend fun test()
}