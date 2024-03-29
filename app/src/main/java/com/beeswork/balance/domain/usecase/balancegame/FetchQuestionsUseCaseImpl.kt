package com.beeswork.balance.domain.usecase.balancegame

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.domain.uistate.balancegame.QuestionItemUIState
import com.beeswork.balance.internal.exception.ServerException
import com.beeswork.balance.internal.mapper.profile.QuestionMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

class FetchQuestionsUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : FetchQuestionsUseCase {

    override suspend fun invoke(): Resource<FetchQuestionsDTO> {
        return try {
            withContext(defaultDispatcher) {
                profileRepository.fetchQuestions()
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }
}