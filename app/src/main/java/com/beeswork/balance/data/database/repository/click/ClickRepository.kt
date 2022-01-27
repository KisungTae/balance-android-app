package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.click.ClickDTO
import kotlinx.coroutines.flow.Flow

interface ClickRepository {

    val clickPageInvalidationFlow: Flow<Click?>

    suspend fun deleteClicks()
    suspend fun saveClick(clickDTO: ClickDTO)
    suspend fun loadClicks(loadSize: Int, startPosition: Int): Resource<List<Click>>
    fun getClickCountFlow(): Flow<Int>


    fun test()
}