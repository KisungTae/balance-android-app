package com.beeswork.balance.domain.usecase.balancegame

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.domain.uistate.balancegame.QuestionItemUIState
import com.beeswork.balance.internal.mapper.profile.QuestionMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class FetchRandomQuestionsUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val questionMapper: QuestionMapper,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : FetchRandomQuestionsUseCase {

    override suspend fun invoke(): Resource<List<QuestionItemUIState>> = withContext(defaultDispatcher) {
        try {
            val response = profileRepository.fetchRandomQuestions()
            return@withContext response.map { questionDTOs ->
                questionDTOs?.map { questionDTO ->
                    questionMapper.toQuestionItemUIState(questionDTO)
                }
            }
        } catch (e: IOException) {
            return@withContext Resource.error(e)
        }
    }
}