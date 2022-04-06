package com.beeswork.balance.data.network.rds.profile

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO

interface ProfileRDS {

    suspend fun fetchProfile(): Resource<ProfileDTO>
    suspend fun saveProfile(profileDTO: ProfileDTO): Resource<EmptyResponse>
    suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyResponse>
    suspend fun fetchQuestions(): Resource<FetchQuestionsDTO>
    suspend fun fetchRandomQuestion(questionIds: List<Int>): Resource<QuestionDTO>
    suspend fun saveBio(height: Int?, about: String): Resource<EmptyResponse>

}