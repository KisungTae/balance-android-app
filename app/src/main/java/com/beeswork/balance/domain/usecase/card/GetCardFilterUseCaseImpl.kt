package com.beeswork.balance.domain.usecase.card

import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.data.database.repository.card.CardRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class GetCardFilterUseCaseImpl(
    private val cardRepository: CardRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): GetCardFilterUseCase {
    override suspend fun invoke(): CardFilter? {
        return withContext(defaultDispatcher) {
            cardRepository.getCardFilter()
        }
    }
}