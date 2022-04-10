package com.beeswork.balance.domain.usecase.balancegame

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.internal.exception.ServerException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

class FetchRandomQuestionUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : FetchRandomQuestionUseCase {

    override suspend fun invoke(questionIds: List<Int>): Resource<QuestionDTO> = withContext(defaultDispatcher) {
        try {
            return@withContext profileRepository.fetchRandomQuestion(questionIds)
        } catch (e: IOException) {
            return@withContext Resource.error(e)
        }
    }
}