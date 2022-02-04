package com.beeswork.balance.data.network.rds.click

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.data.network.response.click.CountClicksDTO
import com.beeswork.balance.data.network.response.click.ListClicksDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import java.util.*

class ClickRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), ClickRDS {
    override suspend fun listClicks(loadSize: Int, startPosition: Int): Resource<ListClicksDTO> {
        return getResult { balanceAPI.listClicks(loadSize, startPosition) }
    }

    override suspend fun fetchClicks(loadSize: Int, lastSwiperId: UUID?): Resource<List<ClickDTO>> {
        return getResult { balanceAPI.fetchClicks(loadSize, lastSwiperId) }
    }

    override suspend fun countClicks(): Resource<CountClicksDTO> {
        return getResult { balanceAPI.countClicks() }
    }
}