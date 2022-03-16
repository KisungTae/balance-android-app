package com.beeswork.balance.domain.usecase.chat

import com.beeswork.balance.data.database.repository.match.MatchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class SyncMatchUseCaseImpl(
    private val matchRepository: MatchRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): SyncMatchUseCase {

    override suspend fun invoke(chatId: UUID) = withContext(defaultDispatcher) {
        matchRepository.syncMatch(chatId)
    }
}