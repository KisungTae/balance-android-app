package com.beeswork.balance.data.database.repository.card

import com.beeswork.balance.data.database.entity.card.Card
import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.card.FetchCardsResponse
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import kotlinx.coroutines.flow.Flow
import java.util.*

interface CardRepository {
    suspend fun like(swipedId: UUID): Resource<FetchQuestionsDTO>
    suspend fun deleteCardFilter()
    suspend fun getCardFilter(): CardFilter?
    suspend fun saveCardFilter(gender: Boolean, minAge: Int, maxAge: Int, distance: Int): Resource<EmptyResponse>
    suspend fun fetchCards(): Resource<List<Card>>
    fun getCardFilterInvalidationFlow(): Flow<Boolean>
}