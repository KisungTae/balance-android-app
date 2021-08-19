package com.beeswork.balance.data.network.rds.swipe

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.data.network.response.swipe.FetchCardsDTO
import com.beeswork.balance.internal.constant.Gender
import java.util.*

interface SwipeRDS {

    suspend fun fetchCards(
        accountId: UUID,
        minAge: Int,
        maxAge: Int,
        gender: Boolean,
        distance: Int,
        pageIndex: Int
    ): Resource<FetchCardsDTO>

    suspend fun swipe(
        accountId: UUID,
        swipedId: UUID
    ): Resource<List<QuestionDTO>>
}