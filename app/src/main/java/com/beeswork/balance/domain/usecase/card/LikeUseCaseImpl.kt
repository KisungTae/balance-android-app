package com.beeswork.balance.domain.usecase.card

import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class LikeUseCaseImpl(
    private val cardRepository: CardRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : LikeUseCase {

    override suspend fun invoke(swipedId: UUID): Resource<FetchQuestionsDTO> {
        return try {
            withContext(defaultDispatcher) {
                cardRepository.like(swipedId)
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }
}