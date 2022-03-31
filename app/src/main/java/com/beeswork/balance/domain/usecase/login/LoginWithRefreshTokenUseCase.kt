package com.beeswork.balance.domain.usecase.login

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.login.LoginDTO

interface LoginWithRefreshTokenUseCase {

    suspend fun invoke(): Resource<LoginDTO>
}