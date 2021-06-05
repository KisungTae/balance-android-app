package com.beeswork.balance.data.database.repository.setting

import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    suspend fun saveFCMToken(token: String)
    suspend fun saveLocation(latitude: Double, longitude: Double)
    suspend fun syncLocation()
    fun getEmailFlow(): Flow<String?>
}