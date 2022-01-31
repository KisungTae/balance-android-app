package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.click.ClickDTO
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ClickRepository {

    val clickPageInvalidationFlow: Flow<Click?>

    suspend fun deleteClicks()
    suspend fun saveClick(clickDTO: ClickDTO)
    suspend fun loadClicks(loadSize: Int, startPosition: Int): Resource<List<Click>>
    suspend fun fetchClicks(loadSize: Int, lastSwiperId: UUID?): Resource<Int>

    fun getClickPageInvalidation(): Flow<Boolean>
    fun getClickCountFlow(): Flow<Int>


    fun test()
}