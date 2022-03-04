package com.beeswork.balance.data.database.repository.card

import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.data.network.response.card.FetchCardsDTO
import java.util.*

interface CardRepository {
    suspend fun like(swipedId: UUID): Resource<List<QuestionDTO>>
    suspend fun deleteCardFilter()
    suspend fun getCardFilter(): CardFilter
    suspend fun saveCardFilter(gender: Boolean, minAge: Int, maxAge: Int, distance: Int)
    suspend fun fetchCards(): Resource<FetchCardsDTO>
    suspend fun prepopulateCardFilter(gender: Boolean)
}