package com.beeswork.balance.domain.usecase.card

import com.beeswork.balance.data.database.entity.card.Card
import com.beeswork.balance.data.network.response.Resource

interface FetchCardsUseCase {

    suspend fun invoke(resetPage: Boolean): Resource<List<Card>>
}