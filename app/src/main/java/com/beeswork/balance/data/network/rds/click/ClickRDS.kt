package com.beeswork.balance.data.network.rds.click

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.ClickDTO
import org.threeten.bp.OffsetDateTime
import java.util.*

interface ClickRDS {
    suspend fun listClicks(
        accountId: UUID?,
        identityToken: UUID?,
        fetchedAt: OffsetDateTime
    ): Resource<List<ClickDTO>>
}