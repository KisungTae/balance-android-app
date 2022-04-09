package com.beeswork.balance.domain.usecase.card

import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.result.ClickResult
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.ClickResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class ClickUseCaseImpl(
    private val matchRepository: MatchRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ClickUseCase {
    override suspend fun invoke(swipedId: UUID, answers: Map<Int, Boolean>): Resource<ClickResult> = withContext(defaultDispatcher) {
        try {
            return@withContext matchRepository.click(swipedId, answers)
        } catch (e: IOException) {
            return@withContext Resource.error(e)
        }
    }
}