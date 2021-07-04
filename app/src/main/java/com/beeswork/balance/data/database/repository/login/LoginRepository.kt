package com.beeswork.balance.data.database.repository.login

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.login.LoginDTO

interface LoginRepository {
    suspend fun socialLogin(loginId: String, accessToken: String): Resource<LoginDTO>
    suspend fun login(): Resource<EmptyResponse>
}