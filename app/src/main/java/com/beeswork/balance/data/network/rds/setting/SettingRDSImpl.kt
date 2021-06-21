package com.beeswork.balance.data.network.rds.setting

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.*
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import org.threeten.bp.OffsetDateTime
import java.util.*

class SettingRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), SettingRDS {

    override suspend fun postPushSettings(
        accountId: UUID?,
        identityToken: UUID?,
        matchPush: Boolean?,
        clickedPush: Boolean?,
        chatMessagePush: Boolean?
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postPushSettings(
                PostPushSettingsBody(accountId, identityToken, matchPush, clickedPush, chatMessagePush)
            )
        }
    }

    override suspend fun getEmail(accountId: UUID?, identityToken: UUID?): Resource<String> {
        return getResult { balanceAPI.getEmail(accountId, identityToken) }
    }

    override suspend fun postEmail(accountId: UUID?, identityToken: UUID?, email: String): Resource<EmptyResponse> {
        return getResult { balanceAPI.postEmail(PostEmailBody(accountId, identityToken, email)) }
    }

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

    override suspend fun postSettings(accountId: UUID?, identityToken: UUID?): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postSettings(PostSettingsBody(accountId, identityToken))
        }
    }

}