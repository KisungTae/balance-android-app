package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.data.network.response.click.FetchClicksDTO
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ClickRepository {

    val newClickInvalidationFlow: Flow<Click>
    val clickCountInvalidationFlow: Flow<Long?>
    val clickPageInvalidationFlow: Flow<Boolean>

    suspend fun fetchClicks(loadSize: Int, lastSwiperId: UUID?): Resource<FetchClicksDTO>
    suspend fun loadClicks(loadSize: Int, startPosition: Int): List<Click>
    suspend fun saveClick(clickDTO: ClickDTO)
    suspend fun syncClickCount()
    suspend fun deleteClicks()

    fun test()
}