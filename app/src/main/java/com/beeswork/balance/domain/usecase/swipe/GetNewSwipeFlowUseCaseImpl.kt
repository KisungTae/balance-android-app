package com.beeswork.balance.domain.usecase.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GetNewSwipeFlowUseCaseImpl(
    private val swipeRepository: SwipeRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : GetNewSwipeFlowUseCase {

    override suspend fun invoke(): Flow<Swipe> = withContext(defaultDispatcher) {
        return@withContext swipeRepository.newSwipeFlow
    }
}