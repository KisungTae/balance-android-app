package com.beeswork.balance.data.network.rds.card

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.swipe.LikeBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.card.FetchCardsResponse
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import java.util.*

class CardRDSImpl(
    private  val balanceAPI: BalanceAPI
) : BaseRDS(), CardRDS {

    override suspend fun fetchCards(
        minAge: Int,
        maxAge: Int,
        gender: Boolean,
        distance: Int,
        pageIndex: Int
    ): Resource<FetchCardsResponse> {
        return getResult { balanceAPI.recommend(minAge, maxAge, gender, distance, pageIndex) }
    }

    override suspend fun like(swipedId: UUID): Resource<FetchQuestionsDTO> {
        return getResult { balanceAPI.like(LikeBody(swipedId)) }
    }
}