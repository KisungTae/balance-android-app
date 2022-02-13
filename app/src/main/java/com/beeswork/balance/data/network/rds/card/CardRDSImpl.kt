package com.beeswork.balance.data.network.rds.card

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.swipe.LikeBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.data.network.response.card.FetchCardsDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import java.util.*

class CardRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), CardRDS {

    override suspend fun fetchCards(
        minAge: Int,
        maxAge: Int,
        gender: Boolean,
        distance: Int,
        pageIndex: Int
    ): Resource<FetchCardsDTO> {
        return getResult { balanceAPI.recommend(minAge, maxAge, gender, distance, pageIndex) }
    }

    override suspend fun like(swipedId: UUID): Resource<List<QuestionDTO>> {
        return getResult { balanceAPI.like(LikeBody(swipedId)) }
    }
}