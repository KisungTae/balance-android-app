package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.click.ClickDTO
import kotlinx.coroutines.flow.Flow

interface ClickRepository {

    val newClickFlow: Flow<Click>

    suspend fun deleteClicks()
    suspend fun saveClick(clickDTO: ClickDTO)
    suspend fun loadClicks(loadSize: Int, startPosition: Int): List<Click>
    suspend fun fetchClicks(): Resource<EmptyResponse>
    fun getClickInvalidation(): Flow<Boolean>
    fun getClickCount(): Flow<Int>


    fun test()
}