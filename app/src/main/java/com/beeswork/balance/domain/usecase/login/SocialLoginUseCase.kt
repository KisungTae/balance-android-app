package com.beeswork.balance.domain.usecase.login

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.internal.constant.LoginType

interface SocialLoginUseCase {

    suspend fun invoke(loginId: String, accessToken: String, loginType: LoginType): Resource<LoginDTO>
}