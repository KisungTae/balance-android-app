package com.beeswork.balance.data.network.rds.login

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.internal.constant.LoginType
import java.util.*

interface LoginRDS {
    suspend fun socialLogin(
        loginId: String,
        accessToken: String,
        loginType: LoginType
    ): Resource<LoginDTO>
}