package com.beeswork.balance.data.network.rds.click

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.data.network.response.click.CountClicksDTO
import com.beeswork.balance.data.network.response.click.ListClicksDTO
import java.util.*

interface ClickRDS {
    suspend fun listClicks(loadSize: Int, startPosition: Int): Resource<ListClicksDTO>
    suspend fun fetchClicks(loadSize: Int, lastSwiperId: UUID?): Resource<List<ClickDTO>>
    suspend fun countClicks(): Resource<CountClicksDTO>
}