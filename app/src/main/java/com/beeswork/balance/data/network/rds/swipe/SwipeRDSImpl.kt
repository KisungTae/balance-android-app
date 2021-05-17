package com.beeswork.balance.data.network.rds.swipe

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.FetchCardsDTO
import com.beeswork.balance.internal.constant.Gender
import java.util.*

class SwipeRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), SwipeRDS {
    override suspend fun fetchCards(
        accountId: UUID?,
        identityToken: UUID?,
        minAge: Int,
        maxAge: Int,
        gender: Gender,
        distance: Int,
        pageIndex: Int
    ): Resource<FetchCardsDTO> {
        return getResult {
            balanceAPI.recommend(accountId, identityToken, minAge, maxAge, gender, distance, pageIndex)
        }
    }
}