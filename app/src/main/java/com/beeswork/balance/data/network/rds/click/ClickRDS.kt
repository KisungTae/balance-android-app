package com.beeswork.balance.data.network.rds.click

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.click.ClickDTO
import org.threeten.bp.OffsetDateTime

interface ClickRDS {
    suspend fun listClicks(fetchedAt: OffsetDateTime): Resource<List<ClickDTO>>
}