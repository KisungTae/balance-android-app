package com.beeswork.balance.data.database.repository.setting

import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.database.entity.setting.FCMToken
import com.beeswork.balance.data.database.entity.setting.Location
import com.beeswork.balance.data.database.entity.setting.PushSetting
import com.beeswork.balance.data.network.rds.setting.SettingRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
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
    private val pushSettingMapper: PushSettingMapper,
    private val ioDispatcher: CoroutineDispatcher
) : SettingRepository {

    override suspend fun getPushSetting(): PushSetting? {
        return withContext(ioDispatcher) {
            return@withContext pushSettingDAO.getBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun fetchPushSetting(): Resource<PushSetting> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
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
        swipePush: Boolean,
        chatMessagePush: Boolean,
        emailPush: Boolean
    ): Resource<PushSetting> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())

            pushSettingDAO.updateSyncedBy(accountId, false)
            val response = settingRDS.savePushSettings(
                matchPush,
                swipePush,
                chatMessagePush,
                emailPush
            )
            if (response.isSuccess()) {
                pushSettingDAO.updatePushSettingsBy(accountId, matchPush, swipePush, chatMessagePush, emailPush)
                return@withContext Resource.success(null)
            } else {
                pushSettingDAO.updateSyncedBy(accountId, true)
                return@withContext response.map { pushSettingDAO.getBy(accountId) }
            }
        }
    }

    override suspend fun fetchSettings() {
        //TODO: fetch settings like pushsetting
    }


    override suspend fun deleteSettings() {
        withContext(ioDispatcher) {
            pushSettingDAO.deleteBy(preferenceProvider.getAccountId())
            preferenceProvider.delete()
        }
    }

    override suspend fun saveFCMToken(token: String) {
        withContext(Dispatchers.IO) {
            fcmTokenDAO.insert(FCMToken(token))
        }
    }

    override suspend fun saveLocation(latitude: Double, longitude: Double, syncLocation: Boolean) {
        withContext(ioDispatcher) {
            val updatedAt = OffsetDateTime.now()
            locationDAO.insert(Location(latitude, longitude, false, updatedAt))
            if (syncLocation) {
                syncLocation(latitude, longitude, updatedAt)
            }
        }
    }

    override suspend fun syncLocation() {
        withContext(ioDispatcher) {
            locationDAO.getById()?.let { location ->
                if (!location.synced) {
                    syncLocation(location.latitude, location.longitude, location.updatedAt)
                }
            }
        }
    }

    override suspend fun getLocation(): Location? {
        return withContext(ioDispatcher) {
            return@withContext locationDAO.getById()
        }
    }

    private fun syncLocation(latitude: Double, longitude: Double, updatedAt: OffsetDateTime) {
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { c, t -> }) {
            val response = settingRDS.saveLocation(latitude, longitude, updatedAt)
            if (response.isSuccess()) {
                locationDAO.sync(updatedAt)
            }
        }
    }

    override fun getLocationFlow(): Flow<Location?> {
        return locationDAO.getLocationFlowById()
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
}