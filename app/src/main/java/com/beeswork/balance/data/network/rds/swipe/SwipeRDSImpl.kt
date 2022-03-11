package com.beeswork.balance.data.network.rds.swipe

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.data.network.response.swipe.CountSwipesDTO
import com.beeswork.balance.data.network.response.swipe.ListSwipesDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import java.util.*

class SwipeRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), SwipeRDS {

    override suspend fun listSwipes(loadSize: Int, startPosition: Int): Resource<ListSwipesDTO> {
        return getResult { balanceAPI.listSwipes(loadSize, startPosition) }
    }

    override suspend fun fetchSwipes(loadSize: Int, lastSwiperId: UUID?): Resource<ListSwipesDTO> {
        return getResult { balanceAPI.fetchSwipes(loadSize, lastSwiperId) }
    }
}