package com.beeswork.balance.data.network.rds.setting

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.setting.PushSettingDTO
import org.threeten.bp.OffsetDateTime

interface SettingRDS {

    suspend fun fetchPushSetting(): Resource<PushSettingDTO>
    suspend fun deleteAccount(): Resource<EmptyResponse>

    suspend fun savePushSettings(
        matchPush: Boolean,
        clickedPush: Boolean,
        chatMessagePush: Boolean,
        emailPush: Boolean
    ): Resource<EmptyResponse>

    suspend fun saveFCMToken(token: String): Resource<EmptyResponse>
    suspend fun saveLocation(latitude: Double, longitude: Double, updatedAt: OffsetDateTime): Resource<EmptyResponse>
}