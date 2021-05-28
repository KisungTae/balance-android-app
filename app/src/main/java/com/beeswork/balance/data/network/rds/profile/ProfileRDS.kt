package com.beeswork.balance.data.network.rds.profile

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import java.util.*

interface ProfileRDS {

    suspend fun postAbout(
        accountId: UUID?,
        identityToken: UUID?,
        height: Int?,
        about: String
    ): Resource<EmptyResponse>
}