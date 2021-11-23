package com.beeswork.balance.data.network.rds.login

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.login.RefreshAccessTokenBody
import com.beeswork.balance.data.network.request.profile.SaveEmailBody
import com.beeswork.balance.data.network.request.login.SocialLoginBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import java.util.*

class LoginRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), LoginRDS {

    override suspend fun saveEmail(accountId: UUID, email: String): Resource<EmptyResponse> {
        return getResult { balanceAPI.saveEmail(SaveEmailBody(accountId, email)) }
    }

    override suspend fun fetchEmail(accountId: UUID): Resource<String> {
        return getResult { balanceAPI.getEmail(accountId) }
    }

    override suspend fun loginWithRefreshToken(refreshToken: String, accessToken: String): Resource<LoginDTO> {
        return getResult { balanceAPI.loginWithRefreshToken(RefreshAccessTokenBody(accountId, refreshToken)) }
    }

    override suspend fun socialLogin(loginId: String, accessToken: String, loginType: LoginType): Resource<LoginDTO> {
        return getResult { balanceAPI.socialLogin(SocialLoginBody(loginId, accessToken, loginType)) }
    }



}