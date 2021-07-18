package com.beeswork.balance.data.network.rds.profile

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import java.util.*

interface ProfileRDS {

    suspend fun fetchProfile(
        accountId: UUID?,
        identityToken: UUID?
    ): Resource<ProfileDTO>

    suspend fun saveQuestions(
        accountId: UUID?,
        identityToken: UUID?,
        answers: Map<Int, Boolean>
    ): Resource<EmptyResponse>

    suspend fun listQuestions(
        accountId: UUID?,
        identityToken: UUID?
    ): Resource<List<QuestionDTO>>

    suspend fun postAbout(
        accountId: UUID?,
        identityToken: UUID?,
        height: Int?,
        about: String
    ): Resource<EmptyResponse>
}