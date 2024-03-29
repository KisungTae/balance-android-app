package com.beeswork.balance.data.database.repository.login

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.data.network.response.login.RefreshAccessTokenDTO
import com.beeswork.balance.internal.constant.LoginType
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun saveEmail(email: String): Resource<String>
    suspend fun getEmail(): String?
    suspend fun fetchEmail(): Resource<String>
    suspend fun socialLogin(loginId: String, accessToken: String, loginType: LoginType): Resource<LoginDTO>
    suspend fun login(): Resource<EmptyResponse>
    suspend fun deleteLogin()
    suspend fun isEmailSynced(): Boolean
    suspend fun getLoginType(): LoginType?
    suspend fun loginWithRefreshToken(): Resource<LoginDTO>
    suspend fun refreshAccessToken(): Resource<RefreshAccessTokenDTO>
    fun getEmailFlow(): Flow<String?>
}