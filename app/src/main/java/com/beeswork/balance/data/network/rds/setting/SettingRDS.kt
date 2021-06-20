package com.beeswork.balance.data.network.rds.setting

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import org.threeten.bp.OffsetDateTime
import java.util.*

interface SettingRDS {
    suspend fun getEmail(accountId: UUID?, identityToken: UUID?): Resource<String>
    suspend fun postEmail(accountId: UUID?, identityToken: UUID?, email: String): Resource<EmptyResponse>
    suspend fun postFCMToken(accountId: UUID?, identityToken: UUID?, token: String): Resource<EmptyResponse>
    suspend fun postLocation(
        accountId: UUID?,
        identityToken: UUID?,
        latitude: Double,
        longitude: Double,
        updatedAt: OffsetDateTime
    ): Resource<EmptyResponse>

    suspend fun postSettings(
        accountId: UUID?,
        identityToken: UUID?
    ): Resource<EmptyResponse>
}