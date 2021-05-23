package com.beeswork.balance.data.network.rds.swipe

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.ClickBody
import com.beeswork.balance.data.network.request.SwipeBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO
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

    override suspend fun swipe(accountId: UUID?, identityToken: UUID?, swipedId: UUID): Resource<List<QuestionDTO>> {
        return getResult {
            balanceAPI.swipe(SwipeBody(accountId, identityToken, swipedId))
        }
    }
}