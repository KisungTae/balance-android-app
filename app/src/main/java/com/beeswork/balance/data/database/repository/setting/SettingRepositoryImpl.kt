package com.beeswork.balance.data.database.repository.setting

import com.beeswork.balance.data.database.dao.FCMTokenDAO
import com.beeswork.balance.data.database.dao.LocationDAO
import com.beeswork.balance.data.database.dao.SettingDAO
import com.beeswork.balance.data.database.entity.FCMToken
import com.beeswork.balance.data.database.entity.Location
import com.beeswork.balance.data.database.entity.Setting
import com.beeswork.balance.data.database.tuple.PushSettingsTuple
import com.beeswork.balance.data.network.rds.setting.SettingRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime

class SettingRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val fcmTokenDAO: FCMTokenDAO,
    private val locationDAO: LocationDAO,
    private val settingRDS: SettingRDS,
    private val settingDAO: SettingDAO,
    private val ioDispatcher: CoroutineDispatcher
) : SettingRepository {

    override suspend fun saveFCMToken(token: String) {
        withContext(Dispatchers.IO) {
            fcmTokenDAO.insert(FCMToken(token, false))
            val response = settingRDS.postFCMToken(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                token
            )
            if (response.isSuccess()) fcmTokenDAO.sync()
        }
    }

    override suspend fun saveLocation(latitude: Double, longitude: Double) {
        withContext(ioDispatcher) {
            val now = OffsetDateTime.now()
            locationDAO.insert(Location(latitude, longitude, false, now))
            syncLocation(latitude, longitude, now)
        }
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
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { c, t -> }) {
            locationDAO.findById()?.let { location ->
                if (!location.synced)
                    syncLocation(location.latitude, location.longitude, location.updatedAt)
            }
        }
    }

    override suspend fun saveEmail(email: String): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val response = settingRDS.postEmail(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                email
            )
            if (response.isSuccess()) settingDAO.syncEmail(email)
            return@withContext response
        }
    }

    override suspend fun fetchEmail() {
        withContext(ioDispatcher) {
            if (!settingDAO.findById().emailSynced) {
                val response = settingRDS.getEmail(
                    preferenceProvider.getAccountId(),
                    preferenceProvider.getIdentityToken()
                )
                response.data?.let { email -> settingDAO.syncEmail(email) }
            }
        }
    }

    override suspend fun saveMatchPush(matchPush: Boolean): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            settingDAO.updateMatchPush(matchPush)
            val response = postPushSettings(matchPush, null, null)
            if (response.isSuccess()) settingDAO.syncMatchPush()
            else if (response.isError()) settingDAO.revertMatchPush()
            return@withContext response
        }
    }

    override suspend fun saveClickedPush(clickedPush: Boolean): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            settingDAO.updateClickedPush(clickedPush)
            val response = postPushSettings(null, clickedPush, null)
            if (response.isSuccess()) settingDAO.syncClickedPush()
            else if (response.isError()) settingDAO.revertClickedPush()
            return@withContext response
        }
    }

    override suspend fun saveChatMessagePush(chatMessagePush: Boolean): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            settingDAO.updateChatMessagePush(chatMessagePush)
            val response = postPushSettings(null, null, chatMessagePush)
            if (response.isSuccess()) settingDAO.syncChatMessagePush()
            else if (response.isError()) settingDAO.revertChatMessagePush()
            return@withContext response
        }
    }

    override suspend fun getSetting(): Setting {
        return withContext(ioDispatcher) {
            return@withContext settingDAO.findById()
        }
    }

    private suspend fun postPushSettings(
        matchPush: Boolean?,
        clickedPush: Boolean?,
        chatMessagePush: Boolean?
    ): Resource<EmptyResponse> {
        return settingRDS.postPushSettings(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            matchPush,
            clickedPush,
            chatMessagePush
        )
    }

    override fun getEmailFlow(): Flow<String?> {
        return settingDAO.findEmailFlow()
    }

    override suspend fun getPushSettingsFlow(): Flow<PushSettingsTuple> {
        return withContext(ioDispatcher) {
            return@withContext settingDAO.findPushSettingsFlow()
        }
    }

    override suspend fun prepopulateSetting() {
        withContext(ioDispatcher) {
            if (!settingDAO.exist()) settingDAO.insert(Setting())
        }
    }

    override suspend fun getMatchPush(): Boolean {
        return withContext(ioDispatcher) {
            return@withContext settingDAO.findMatchPush()
        }
    }

    override suspend fun getClickedPush(): Boolean {
        return withContext(ioDispatcher) {
            return@withContext settingDAO.findClickedPush()
        }
    }

    override suspend fun getChatMessagePush(): Boolean {
        return withContext(ioDispatcher) {
            return@withContext settingDAO.findChatMessagePush()
        }
    }

    override suspend fun syncPushSettings(
        matchPush: Boolean?,
        clickedPush: Boolean?,
        chatMessagePush: Boolean?
    ): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            return@withContext settingRDS.postPushSettings(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                matchPush,
                clickedPush,
                chatMessagePush
            )
        }
    }

    override suspend fun syncMatchPush() {
        withContext(ioDispatcher) { settingDAO.syncMatchPush() }
    }

    override suspend fun syncClickedPush() {
        withContext(ioDispatcher) { settingDAO.syncClickedPush() }
    }

    override suspend fun syncChatMessagePush() {
        withContext(ioDispatcher) { settingDAO.syncChatMessagePush() }
    }
}