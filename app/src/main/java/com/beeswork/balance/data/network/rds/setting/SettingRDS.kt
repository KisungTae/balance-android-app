package com.beeswork.balance.data.network.rds.setting

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.setting.PushSettingDTO
import org.threeten.bp.OffsetDateTime
import java.util.*

interface SettingRDS {

    suspend fun fetchPushSetting(
        accountId: UUID,
        identityToken: UUID
    ): Resource<PushSettingDTO>

    suspend fun deleteAccount(
        accountId: UUID,
        identityToken: UUID
    ): Resource<EmptyResponse>

    suspend fun postPushSettings(
        accountId: UUID,
        identityToken: UUID,
        matchPush: Boolean,
        clickedPush: Boolean,
        chatMessagePush: Boolean,
        emailPush: Boolean
    ): Resource<EmptyResponse>

    suspend fun postFCMToken(accountId: UUID, identityToken: UUID, token: String): Resource<EmptyResponse>
    suspend fun postLocation(
        accountId: UUID,
        identityToken: UUID,
        latitude: Double,
        longitude: Double,
        updatedAt: OffsetDateTime
    ): Resource<EmptyResponse>
}