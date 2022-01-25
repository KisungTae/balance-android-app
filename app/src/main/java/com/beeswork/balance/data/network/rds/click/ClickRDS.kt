package com.beeswork.balance.data.network.rds.click

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.click.ClickDTO

interface ClickRDS {
    suspend fun listClicks(loadSize: Int, startPosition: Int): Resource<List<ClickDTO>>
}