package com.beeswork.balance.data.network.rds.profile

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.PostAnswersBody
import com.beeswork.balance.data.network.request.SaveAboutBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import java.util.*

class ProfileRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ProfileRDS {

    override suspend fun saveQuestions(
        accountId: UUID?,
        identityToken: UUID?,
        answers: Map<Int, Boolean>
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postAnswers(PostAnswersBody(accountId, identityToken, answers))
        }
    }

    override suspend fun listQuestions(accountId: UUID?, identityToken: UUID?): Resource<List<QuestionDTO>> {
        return getResult {
            balanceAPI.listQuestions(accountId, identityToken)
        }
    }

    override suspend fun postAbout(
        accountId: UUID?,
        identityToken: UUID?,
        height: Int?,
        about: String
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postAbout(SaveAboutBody(accountId, identityToken, height, about))
        }
    }


}