package com.beeswork.balance.data.network.rds.profile

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import org.threeten.bp.LocalDate

interface ProfileRDS {

    suspend fun fetchProfile(): Resource<ProfileDTO>
    suspend fun saveProfile(
        name: String,
        gender: Boolean,
        birthDate: LocalDate,
        height: Int?,
        about: String?,
        latitude: Double,
        longitude: Double
    ): Resource<EmptyResponse>
    suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyResponse>
    suspend fun fetchQuestions(): Resource<FetchQuestionsDTO>
    suspend fun fetchRandomQuestion(questionIds: List<Int>): Resource<QuestionDTO>
    suspend fun saveBio(height: Int?, about: String): Resource<EmptyResponse>


}