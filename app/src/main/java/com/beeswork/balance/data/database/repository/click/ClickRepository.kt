package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.click.ClickDTO
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ClickRepository {

    val newClickInvalidationFlow: Flow<Click?>

    suspend fun fetchClicks(loadSize: Int, lastSwiperId: UUID?): Resource<Int>
    suspend fun loadClicks(loadSize: Int, startPosition: Int): List<Click>
    suspend fun saveClick(clickDTO: ClickDTO)
    suspend fun deleteClicks()

    fun getClickPageInvalidationFlow(): Flow<Boolean>
    fun getClickCountFlow(): Flow<Int>


    fun test()
}