package com.beeswork.balance.data.database.repository.setting

interface SettingRepository {
    suspend fun saveFCMToken(token: String)
    suspend fun saveLocation(latitude: Double, longitude: Double)
}