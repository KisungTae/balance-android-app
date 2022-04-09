package com.beeswork.balance.data.network.rds.card

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.data.network.response.card.FetchCardsDTO
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import java.util.*

interface CardRDS {
    suspend fun fetchCards(minAge: Int, maxAge: Int, gender: Boolean, distance: Int, pageIndex: Int): Resource<FetchCardsDTO>
    suspend fun like(swipedId: UUID): Resource<FetchQuestionsDTO>
}