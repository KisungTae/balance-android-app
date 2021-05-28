package com.beeswork.balance.data.network.rds.profile

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.SaveAboutBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import java.util.*

class ProfileRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ProfileRDS {

    override suspend fun postAbout(
        accountId: UUID?,
        identityToken: UUID?,
        height: Int?,
        about: String
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postAbout(SaveAboutBody(accountId, identityToken, height, about))
        }
    }


}