package com.beeswork.balance.data.network.rds.login

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.login.LoginWithRefreshTokenBody
import com.beeswork.balance.data.network.request.login.RefreshAccessTokenBody
import com.beeswork.balance.data.network.request.profile.SaveEmailBody
import com.beeswork.balance.data.network.request.login.SocialLoginBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.data.network.response.login.RefreshAccessTokenDTO
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.provider.preference.PreferenceProvider

class LoginRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), LoginRDS {

    override suspend fun saveEmail(email: String): Resource<EmptyResponse> {
        return getResult { balanceAPI.saveEmail(SaveEmailBody(email)) }
    }

    override suspend fun fetchEmail(): Resource<String> {
        return getResult { balanceAPI.getEmail() }
    }

    override suspend fun loginWithRefreshToken(accessToken: String, refreshToken: String, fcmToken: String?): Resource<LoginDTO> {
        return getResult { balanceAPI.loginWithRefreshToken(LoginWithRefreshTokenBody(accessToken, refreshToken, fcmToken)) }
    }

    override suspend fun socialLogin(loginId: String, accessToken: String, loginType: LoginType, pushToken: String?): Resource<LoginDTO> {
        return getResult { balanceAPI.socialLogin(SocialLoginBody(loginId, accessToken, loginType, pushToken)) }
    }

    override suspend fun refreshAccessToken(accessToken: String, refreshToken: String): Resource<RefreshAccessTokenDTO> {
        return getResult { balanceAPI.refreshAccessToken(RefreshAccessTokenBody(accessToken, refreshToken)) }
    }

}