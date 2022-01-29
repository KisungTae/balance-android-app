package com.beeswork.balance.data.network.rds.click

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.click.ClickDTO
import java.util.*

interface ClickRDS {
    suspend fun listClicks(loadSize: Int, startPosition: Int): Resource<List<ClickDTO>>
    suspend fun fetchClicks(loadSize: Int, lastItemId: UUID): Resource<List<ClickDTO>>
}