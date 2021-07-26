package com.beeswork.balance.data.database.repository.login

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.internal.constant.LoginType
import kotlinx.coroutines.flow.Flow
import java.util.*

interface LoginRepository {
    suspend fun saveEmail(email: String?, loginType: LoginType)
    suspend fun fetchEmail(): Resource<EmptyResponse>
    suspend fun socialLogin(loginId: String, accessToken: String, loginType: LoginType): Resource<LoginDTO>
    suspend fun login(): Resource<EmptyResponse>
    suspend fun deleteLogin()

    fun getEmailFlow(): Flow<String?>
}