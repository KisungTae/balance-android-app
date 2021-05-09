package com.beeswork.balance.data.network.rds.click

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.ClickDTO
import org.threeten.bp.OffsetDateTime
import java.util.*

class ClickRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ClickRDS {
    override suspend fun listClicks(
        accountId: UUID?,
        identityToken: UUID?,
        fetchedAt: OffsetDateTime
    ): Resource<List<ClickDTO>> {
        return getResult {
            balanceAPI.listClicks(accountId, identityToken, fetchedAt)
        }
    }
}