package com.beeswork.balance.data.database.repository.login

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.internal.constant.LoginType

interface LoginRepository {
    suspend fun socialLogin(loginId: String, accessToken: String, loginType: LoginType): Resource<LoginDTO>
    suspend fun login(): Resource<EmptyResponse>
}