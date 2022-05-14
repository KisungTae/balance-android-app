package com.beeswork.balance.domain.usecase.card

import com.beeswork.balance.data.database.repository.card.CardRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IncrementReadByIndexUseCaseImpl(
    private val cardRepository: CardRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : IncrementReadByIndexUseCase {

    override suspend fun invoke() = withContext(defaultDispatcher) {
        cardRepository.incrementReadByIndex()
    }

}