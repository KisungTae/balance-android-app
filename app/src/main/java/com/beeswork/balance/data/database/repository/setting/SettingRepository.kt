package com.beeswork.balance.data.database.repository.setting

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    suspend fun saveFCMToken(token: String)
    suspend fun saveLocation(latitude: Double, longitude: Double)
    suspend fun syncLocation()
    suspend fun saveEmail(email: String): Resource<EmptyResponse>
    suspend fun fetchEmail()
    fun getEmailFlow(): Flow<String?>
}