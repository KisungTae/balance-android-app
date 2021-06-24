package com.beeswork.balance.data.database.repository.setting

import com.beeswork.balance.data.database.entity.Setting
import com.beeswork.balance.data.database.tuple.PushSettingsTuple
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    suspend fun saveFCMToken(token: String)
    suspend fun saveLocation(latitude: Double, longitude: Double)
    suspend fun syncLocation()
    suspend fun saveEmail(email: String): Resource<EmptyResponse>
    suspend fun fetchEmail()
    suspend fun saveMatchPush(matchPush: Boolean): Resource<EmptyResponse>
    suspend fun saveClickedPush(clickedPush: Boolean): Resource<EmptyResponse>
    suspend fun saveChatMessagePush(chatMessagePush: Boolean): Resource<EmptyResponse>
    suspend fun getSetting(): Setting
    fun getEmailFlow(): Flow<String?>
    suspend fun getPushSettingsFlow(): Flow<PushSettingsTuple>
    suspend fun prepopulateSetting()
    suspend fun getMatchPush(): Boolean
    suspend fun getClickedPush(): Boolean
    suspend fun getChatMessagePush(): Boolean
    suspend fun syncPushSettings(
        matchPush: Boolean?,
        clickedPush: Boolean?,
        chatMessagePush: Boolean?
    ): Resource<EmptyResponse>


    suspend fun syncMatchPush()
    suspend fun syncClickedPush()
    suspend fun syncChatMessagePush()

}