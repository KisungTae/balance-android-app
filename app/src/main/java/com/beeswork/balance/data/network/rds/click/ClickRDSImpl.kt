package com.beeswork.balance.data.network.rds.click

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider

class ClickRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), ClickRDS {
    override suspend fun listClicks(loadSize: Int, startPosition: Int): Resource<List<ClickDTO>> {
        return getResult { balanceAPI.listClicks(loadSize, startPosition) }
    }
}