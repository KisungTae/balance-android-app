package com.beeswork.balance.data.database.repository.setting

import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.database.entity.FetchInfo
import com.beeswork.balance.data.database.entity.setting.Location
import com.beeswork.balance.data.database.entity.setting.PushSetting
import com.beeswork.balance.data.network.rds.setting.SettingRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.mapper.setting.PushSettingMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime

class SettingRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val fcmTokenDAO: FCMTokenDAO,
    private val locationDAO: LocationDAO,
    private val settingRDS: SettingRDS,
    private val pushSettingDAO: PushSettingDAO,
    private val fetchInfoDAO: FetchInfoDAO,
    private val pushSettingMapper: PushSettingMapper,
    private val ioDispatcher: CoroutineDispatcher
) : SettingRepository {

    override suspend fun getPushSetting(): PushSetting? {
        return withContext(ioDispatcher) {
            return@withContext pushSettingDAO.findByAccountId(preferenceProvider.getAccountId())
        }
    }

    override suspend fun fetchPushSetting(): Resource<PushSetting> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
                ?: return@withContext Resource.error(ExceptionCode.ACCOUNT_ID_NOT_FOUND_EXCEPTION)

            val response = settingRDS.fetchPushSetting()

            if (response.isSuccess()) response.data?.let { pushSettingDTO ->
                val pushSetting = pushSettingMapper.toPushSetting(accountId, pushSettingDTO)
                pushSettingDAO.insert(pushSetting)
                return@withContext response.map { pushSetting }
            }
            return@withContext response.map { null }
        }
    }

    override suspend fun savePushSetting(
        matchPush: Boolean,
        clickedPush: Boolean,
        chatMessagePush: Boolean,
        emailPush: Boolean
    ): Resource<PushSetting> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
                ?: return@withContext Resource.error(ExceptionCode.ACCOUNT_ID_NOT_FOUND_EXCEPTION)

            pushSettingDAO.updateSynced(accountId, false)
            val response = settingRDS.savePushSettings(
                matchPush,
                clickedPush,
                chatMessagePush,
                emailPush
            )
            if (response.isSuccess()) {
                pushSettingDAO.updatePushSettings(accountId, matchPush, clickedPush, chatMessagePush, emailPush)
                return@withContext Resource.success(null)
            } else {
                pushSettingDAO.updateSynced(accountId, true)
                return@withContext response.map { pushSettingDAO.findByAccountId(accountId) }
            }
        }
    }

    override suspend fun fetchSettings() {
        //TODO: fetch settings like pushsetting
    }


    override suspend fun deleteSettings() {
        withContext(ioDispatcher) {
            pushSettingDAO.delete(preferenceProvider.getAccountId())
            preferenceProvider.delete()
        }
    }

    override suspend fun saveFCMToken(token: String) {
        withContext(Dispatchers.IO) {
//            fcmTokenDAO.insert(FCMToken(token, posted = false))
//            preferenceProvider.getAccountId()?.let { accountId ->
//                val response = settingRDS.saveFCMToken(accountId, token)
//                if (response.isSuccess()) fcmTokenDAO.sync()
//            }
        }
    }

    override suspend fun syncFCMTokenAsync() {
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { c, t -> }) {
            fcmTokenDAO.findById()?.let { fcmToken ->
                settingRDS.saveFCMToken(fcmToken)
            }
        }
    }

    override suspend fun saveLocation(latitude: Double, longitude: Double) {
        withContext(ioDispatcher) {
            val updatedAt = OffsetDateTime.now()
            locationDAO.insert(Location(latitude, longitude, false, updatedAt, true))
            syncLocation(latitude, longitude, updatedAt)
        }
    }

    override suspend fun syncLocation() {
        withContext(ioDispatcher) {
            locationDAO.findById()?.let { location ->
                if (!location.synced) syncLocation(location.latitude, location.longitude, location.updatedAt)
            }
        }
    }

    private fun syncLocation(latitude: Double, longitude: Double, updatedAt: OffsetDateTime) {
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { c, t -> }) {
            val response = settingRDS.saveLocation(
                latitude,
                longitude,
                updatedAt
            )
            if (response.isSuccess()) locationDAO.sync(updatedAt)
        }
    }

    override suspend fun saveLocationPermissionResult(granted: Boolean) {
        withContext(ioDispatcher) {
            locationDAO.updateGranted(granted)
        }
    }

    override suspend fun getLocationPermissionResultFlow(): Flow<Boolean?> {
        return withContext(ioDispatcher) {
            return@withContext locationDAO.findGrantedAsFlow()
        }
    }

    override fun getLocationFlow(): Flow<Location?> {
        return locationDAO.findLocationFlow()
    }

    override suspend fun deleteAccount(): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val response = settingRDS.deleteAccount()
            if (response.isSuccess()) {
                //TODO: implement when success
            }
            return@withContext response
        }
    }

    override suspend fun prepopulateFetchInfo() {
        withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
            if (!fetchInfoDAO.existByAccountId(accountId) && accountId != null)
                fetchInfoDAO.insert(FetchInfo(accountId))
        }
    }
}