package com.beeswork.balance.data.network.rds.login

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.login.LoginDTO
import java.util.*

interface LoginRDS {
    suspend fun socialLogin(
        accountId: UUID?,
        identityToken: UUID?,
        loginId: String,
        accessToken: String
    ): Resource<LoginDTO>
}