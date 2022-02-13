package com.beeswork.balance.data.network.rds.swipe

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.data.network.response.swipe.CountSwipesDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import java.util.*

class SwipeRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), SwipeRDS {
    override suspend fun listSwipes(loadSize: Int, startPosition: Int): Resource<List<SwipeDTO>> {
        return getResult { balanceAPI.listSwipes(loadSize, startPosition) }
    }

    override suspend fun fetchSwipes(loadSize: Int, lastSwiperId: UUID?): Resource<List<SwipeDTO>> {
        return getResult { balanceAPI.fetchSwipes(loadSize, lastSwiperId) }
    }

    override suspend fun countSwipes(): Resource<CountSwipesDTO> {
        return getResult { balanceAPI.countSwipes() }
    }
}