package com.beeswork.balance.data.network.rds.login

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.SocialLoginBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.login.LoginDTO
import java.util.*

class LoginRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), LoginRDS {
    override suspend fun socialLogin(
        accountId: UUID?,
        identityToken: UUID?,
        loginId: String,
        accessToken: String
    ): Resource<LoginDTO> {
        return getResult {
            balanceAPI.socialLogin(SocialLoginBody(accountId, identityToken, loginId, accessToken))
        }
    }

}