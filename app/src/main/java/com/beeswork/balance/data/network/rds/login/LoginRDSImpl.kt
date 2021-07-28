package com.beeswork.balance.data.network.rds.login

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.PostEmailBody
import com.beeswork.balance.data.network.request.SocialLoginBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.internal.constant.LoginType
import java.util.*

class LoginRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), LoginRDS {
    override suspend fun saveEmail(accountId: UUID, identityToken: UUID, email: String): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postEmail(PostEmailBody(accountId, identityToken, email))
        }
    }

    override suspend fun fetchEmail(accountId: UUID, identityToken: UUID): Resource<String> {
        return getResult {
            balanceAPI.getEmail(accountId, identityToken)
        }
    }

    override suspend fun socialLogin(
        loginId: String,
        accessToken: String,
        loginType: LoginType
    ): Resource<LoginDTO> {
        return getResult {
            balanceAPI.socialLogin(SocialLoginBody(loginId, accessToken, loginType))
        }
    }

}