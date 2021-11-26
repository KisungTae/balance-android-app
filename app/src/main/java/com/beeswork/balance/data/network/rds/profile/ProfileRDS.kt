package com.beeswork.balance.data.network.rds.profile

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO

interface ProfileRDS {
    suspend fun fetchProfile(): Resource<ProfileDTO>
    suspend fun saveQuestions(answers: Map<Int, Boolean>): Resource<EmptyResponse>
    suspend fun listQuestions(): Resource<List<QuestionDTO>>
    suspend fun saveAbout(height: Int?, about: String): Resource<EmptyResponse>
}