package com.beeswork.balance.data.database.repository.profile

import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.database.entity.Profile
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.QuestionDTO

interface ProfileRepository {
    suspend fun fetchProfile(): Profile?
    suspend fun saveAbout(height: Int?, about: String): Resource<EmptyResponse>
    suspend fun fetchQuestions(): Resource<List<QuestionDTO>>
    suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyResponse>
    fun test()
}
