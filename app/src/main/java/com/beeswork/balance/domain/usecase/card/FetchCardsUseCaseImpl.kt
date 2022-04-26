package com.beeswork.balance.domain.usecase.card

import com.beeswork.balance.data.database.entity.card.Card
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.data.network.response.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class FetchCardsUseCaseImpl(
    private val cardRepository: CardRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): FetchCardsUseCase {

    override suspend fun invoke(): Resource<List<Card>> {
        return try {
            withContext(defaultDispatcher) {
                cardRepository.fetchCards()
            }
        } catch (e: IOException) {
            return Resource.error(e)
        }
    }
}