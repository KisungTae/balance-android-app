package com.beeswork.balance.data.network.rds.profile

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.profile.SaveAnswersBody
import com.beeswork.balance.data.network.request.profile.SaveBioBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO

class ProfileRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ProfileRDS {

    override suspend fun fetchProfile(): Resource<ProfileDTO> {
        return getResult { balanceAPI.fetchProfile() }
    }

    override suspend fun saveProfile(profileDTO: ProfileDTO): Resource<EmptyResponse> {
        return getResult { balanceAPI.saveProfile(profileDTO) }
    }

    override suspend fun saveQuestions(answers: Map<Int, Boolean>): Resource<EmptyResponse> {
        return getResult { balanceAPI.saveAnswers(SaveAnswersBody(answers)) }
    }

    override suspend fun listQuestions(): Resource<List<QuestionDTO>> {
        return getResult { balanceAPI.listQuestions() }
    }

    override suspend fun saveBio(height: Int?, about: String): Resource<EmptyResponse> {
        return getResult { balanceAPI.saveBio(SaveBioBody(height, about)) }
    }


}