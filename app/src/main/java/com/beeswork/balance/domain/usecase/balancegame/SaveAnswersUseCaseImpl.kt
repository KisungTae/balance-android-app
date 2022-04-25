package com.beeswork.balance.domain.usecase.balancegame

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.exception.ServerException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

class SaveAnswersUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveAnswersUseCase {

    override suspend fun invoke(answers: Map<Int, Boolean>): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                profileRepository.saveAnswers(answers)
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }
}