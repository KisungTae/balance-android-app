package com.beeswork.balance.data.network.rds.swipe

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.swipe.SwipeBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.data.network.response.swipe.FetchCardsDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import java.util.*

class SwipeRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), SwipeRDS {

    override suspend fun fetchCards(
        minAge: Int,
        maxAge: Int,
        gender: Boolean,
        distance: Int,
        pageIndex: Int
    ): Resource<FetchCardsDTO> {
        return getResult { balanceAPI.recommend(minAge, maxAge, gender, distance, pageIndex) }
    }

    override suspend fun swipe(swipedId: UUID): Resource<List<QuestionDTO>> {
        return getResult { balanceAPI.swipe(SwipeBody(swipedId)) }
    }
}