package com.beeswork.balance.data.network.rds.setting

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.PostFCMTokenBody
import com.beeswork.balance.data.network.request.PostLocationBody
import com.beeswork.balance.data.network.request.PostSettingsBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import org.threeten.bp.OffsetDateTime
import java.util.*

class SettingRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), SettingRDS {

    override suspend fun postFCMToken(accountId: UUID?, identityToken: UUID?, token: String): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postFCMToken(PostFCMTokenBody(accountId, identityToken, token))
        }
    }

    override suspend fun postLocation(
        accountId: UUID?,
        identityToken: UUID?,
        latitude: Double,
        longitude: Double,
        updatedAt: OffsetDateTime
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postLocation(PostLocationBody(accountId, identityToken, latitude, longitude, updatedAt))
        }
    }

    override suspend fun postSettings(accountId: UUID?, identityToken: UUID?, email: String?): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postSettings(PostSettingsBody(accountId, identityToken, email))
        }
    }

}