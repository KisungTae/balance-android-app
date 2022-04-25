package com.beeswork.balance.domain.usecase.card

import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class SaveCardFilterUseCaseImpl(
    private val cardRepository: CardRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): SaveCardFilterUseCase {

    override suspend fun invoke(gender: Boolean, minAge: Int, maxAge: Int, distance: Int): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                cardRepository.saveCardFilter(gender, minAge, maxAge, distance)
            }
        } catch (e: IOException) {
            return Resource.error(e)
        }
    }
}