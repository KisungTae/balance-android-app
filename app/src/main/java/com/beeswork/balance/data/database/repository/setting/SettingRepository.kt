package com.beeswork.balance.data.database.repository.setting

import com.beeswork.balance.data.database.entity.PushSetting
import com.beeswork.balance.data.database.tuple.LocationTuple
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.flow.Flow

interface SettingRepository {

    suspend fun getPushSetting(): PushSetting?
    suspend fun fetchPushSetting(): Resource<PushSetting>
    suspend fun savePushSetting(
        matchPush: Boolean,
        clickedPush: Boolean,
        chatMessagePush: Boolean,
        emailPush: Boolean
    ): Resource<PushSetting>

    suspend fun fetchSettings()
    suspend fun deleteSettings()

    suspend fun saveFCMToken(token: String)
    suspend fun saveLocation(latitude: Double, longitude: Double)
    suspend fun syncLocation()

    fun getLocationFlow(): Flow<LocationTuple?>
    suspend fun deleteAccount(): Resource<EmptyResponse>
    suspend fun prepopulateFetchInfo()

}