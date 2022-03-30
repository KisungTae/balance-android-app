package com.beeswork.balance.domain.usecase.chat

import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.UnmatchDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class UnmatchUseCaseImpl(
    private val matchRepository: MatchRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : UnmatchUseCase {
    override suspend fun onInvoke(chatId: UUID, swipedId: UUID): Resource<UnmatchDTO> = withContext(defaultDispatcher) {
        matchRepository.unmatch(chatId, swipedId)
    }
}