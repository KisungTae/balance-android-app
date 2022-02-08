package com.beeswork.balance.data.network.rds.login

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.data.network.response.login.RefreshAccessTokenDTO
import com.beeswork.balance.internal.constant.LoginType

interface LoginRDS {

    suspend fun saveEmail(email: String): Resource<EmptyResponse>
    suspend fun fetchEmail(): Resource<String>
    suspend fun loginWithRefreshToken(accessToken: String, refreshToken: String, fcmToken: String?): Resource<LoginDTO>
    suspend fun socialLogin(loginId: String, accessToken: String, loginType: LoginType, pushToken: String?): Resource<LoginDTO>
    suspend fun refreshAccessToken(): Resource<RefreshAccessTokenDTO>
}