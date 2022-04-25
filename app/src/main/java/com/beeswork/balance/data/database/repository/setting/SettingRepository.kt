package com.beeswork.balance.data.database.repository.setting

import com.beeswork.balance.data.database.entity.setting.Location
import com.beeswork.balance.data.database.entity.setting.PushSetting
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.flow.Flow

interface SettingRepository {

    suspend fun getPushSetting(): PushSetting?
    suspend fun fetchPushSetting(): Resource<PushSetting>
    suspend fun savePushSetting(
        matchPush: Boolean,
        swipePush: Boolean,
        chatMessagePush: Boolean,
        emailPush: Boolean
    ): Resource<PushSetting>

    suspend fun fetchSettings()
    suspend fun deleteSettings()

    suspend fun saveFCMToken(token: String)

    suspend fun saveLocation(latitude: Double, longitude: Double, syncLocation: Boolean)
    suspend fun syncLocation()
    suspend fun getLocation(): Location?
    fun getLocationFlow(): Flow<Location?>

    suspend fun deleteAccount(): Resource<EmptyResponse>

}