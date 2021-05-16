package com.beeswork.balance.data.database.repository.setting

import com.beeswork.balance.data.database.dao.FCMTokenDAO
import com.beeswork.balance.data.database.dao.LocationDAO
import com.beeswork.balance.data.database.entity.FCMToken
import com.beeswork.balance.data.database.entity.Location
import com.beeswork.balance.data.network.rds.setting.SettingRDS
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime

class SettingRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val fcmTokenDAO: FCMTokenDAO,
    private val locationDAO: LocationDAO,
    private val settingRDS: SettingRDS
) : SettingRepository {

    override suspend fun saveFCMToken(token: String) {
        fcmTokenDAO.insert(FCMToken(token, false))
        val response = settingRDS.postFCMToken(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            token
        )
        if (response.isSuccess()) fcmTokenDAO.sync()
    }

    override suspend fun saveLocation(latitude: Double, longitude: Double) {
        val now = OffsetDateTime.now()
        locationDAO.insert(Location(latitude, longitude, false, now))
        syncLocation(latitude, longitude, now)
    }

    private suspend fun syncLocation(latitude: Double, longitude: Double, updatedAt: OffsetDateTime) {
        val response = settingRDS.postLocation(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            latitude,
            longitude,
            updatedAt
        )
        if (response.isSuccess()) locationDAO.sync(updatedAt)
    }

    override suspend fun syncLocation() {
        CoroutineScope(Dispatchers.IO).launch(CoroutineExceptionHandler { c, t -> }) {
            locationDAO.findById()?.let { location ->
                if (!location.synced)
                    syncLocation(location.latitude, location.longitude, location.updatedAt)
            }
        }
    }
}