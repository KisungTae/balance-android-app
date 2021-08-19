package com.beeswork.balance.data.network.rds.profile

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.SaveAnswersBody
import com.beeswork.balance.data.network.request.SaveEmailBody
import com.beeswork.balance.data.network.request.SaveAboutBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import java.util.*

class ProfileRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ProfileRDS {

    override suspend fun fetchProfile(accountId: UUID): Resource<ProfileDTO> {
        return getResult { balanceAPI.fetchProfile(accountId) }
    }

    override suspend fun saveQuestions(accountId: UUID, answers: Map<Int, Boolean>): Resource<EmptyResponse> {
        return getResult { balanceAPI.saveAnswers(SaveAnswersBody(accountId, answers)) }
    }

    override suspend fun listQuestions(accountId: UUID): Resource<List<QuestionDTO>> {
        return getResult { balanceAPI.listQuestions(accountId) }
    }

    override suspend fun saveAbout(accountId: UUID, height: Int?, about: String): Resource<EmptyResponse> {
        return getResult { balanceAPI.postAbout(SaveAboutBody(accountId, height, about)) }
    }


}