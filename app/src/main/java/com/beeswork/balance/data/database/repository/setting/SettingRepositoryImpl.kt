package com.beeswork.balance.data.database.repository.setting

import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.database.tuple.LocationTuple
import com.beeswork.balance.data.database.tuple.PushSettingsTuple
import com.beeswork.balance.data.network.rds.setting.SettingRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.setting.SettingMapper
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
    private val fetchInfoDAO: FetchInfoDAO,
    private val settingMapper: SettingMapper,
    private val ioDispatcher: CoroutineDispatcher
) : SettingRepository {

    override suspend fun fetchSetting(): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
            val isSynced = settingDAO.isSynced(accountId) ?: false
            if (isSynced) {
                val response = settingRDS.fetchSetting(
                    preferenceProvider.getAccountId(),
                    preferenceProvider.getIdentityToken()
                )
                response.data?.let { settingDTO ->
                    val setting = settingMapper.toSetting(accountId, settingDTO, true)
                    settingDAO.insert(setting)
                }
                return@withContext response.toEmptyResponse()
            } else return@withContext Resource.success(EmptyResponse())
        }
    }

    override suspend fun deleteSettings() {
        withContext(ioDispatcher) {
            fcmTokenDAO.deleteAll()
            locationDAO.deleteAll()
            settingDAO.deleteAll()
            preferenceProvider.delete()
        }
    }

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


    override suspend fun saveMatchPush(matchPush: Boolean): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
//            settingDAO.updateMatchPush(matchPush)
            val response = postPushSettings(matchPush, null, null)
//            if (response.isSuccess()) settingDAO.syncMatchPush()
//            else if (response.isError()) settingDAO.revertMatchPush()
            return@withContext response
        }
    }

    override suspend fun saveClickedPush(clickedPush: Boolean): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
//            settingDAO.updateClickedPush(clickedPush)
            val response = postPushSettings(null, clickedPush, null)
//            if (response.isSuccess()) settingDAO.syncClickedPush()
//            else if (response.isError()) settingDAO.revertClickedPush()
            return@withContext response
        }
    }

    override suspend fun saveChatMessagePush(chatMessagePush: Boolean): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
//            settingDAO.updateChatMessagePush(chatMessagePush)
            val response = postPushSettings(null, null, chatMessagePush)
//            if (response.isSuccess()) settingDAO.syncChatMessagePush()
//            else if (response.isError()) settingDAO.revertChatMessagePush()
            return@withContext response
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

    override suspend fun getPushSettingsFlow(): Flow<PushSettingsTuple> {
        return withContext(ioDispatcher) {
            return@withContext settingDAO.findPushSettingsFlow(preferenceProvider.getAccountId())
        }
    }

    override suspend fun prepopulateSetting() {
        withContext(ioDispatcher) {
//            if (!settingDAO.exist()) settingDAO.insert(Setting())
        }
    }

    override suspend fun getMatchPush(): Boolean {
        return withContext(ioDispatcher) {
//            return@withContext settingDAO.findMatchPush()
            return@withContext true
        }
    }

    override suspend fun getClickedPush(): Boolean {
        return withContext(ioDispatcher) {
//            return@withContext settingDAO.findClickedPush()
            return@withContext true
        }
    }

    override suspend fun getChatMessagePush(): Boolean {
        return withContext(ioDispatcher) {
//            return@withContext settingDAO.findChatMessagePush()
            return@withContext true
        }
    }

    override suspend fun syncPushSettings(
        matchPush: Boolean?,
        clickedPush: Boolean?,
        chatMessagePush: Boolean?
    ): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val response = settingRDS.postPushSettings(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                matchPush,
                clickedPush,
                chatMessagePush
            )
//            if (response.isError()) {
//                matchPush?.let { settingDAO.revertMatchPush() }
//                clickedPush?.let { settingDAO.revertClickedPush() }
//                chatMessagePush?.let { settingDAO.revertChatMessagePush() }
//            } else if (response.isSuccess()) {
//                matchPush?.let { settingDAO.syncMatchPush() }
//                clickedPush?.let { settingDAO.syncClickedPush() }
//                chatMessagePush?.let { settingDAO.syncChatMessagePush() }
//            }
            return@withContext response
        }
    }

    override suspend fun syncMatchPush() {
//        withContext(ioDispatcher) { settingDAO.syncMatchPush() }
    }

    override suspend fun syncClickedPush() {
//        withContext(ioDispatcher) { settingDAO.syncClickedPush() }
    }

    override suspend fun syncChatMessagePush() {
//        withContext(ioDispatcher) { settingDAO.syncChatMessagePush() }
    }

    override fun getLocationFlow(): Flow<LocationTuple?> {
        return settingDAO.findLocationFlow()
    }

    override suspend fun deleteAccount(): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val response = settingRDS.deleteAccount(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken()
            )
            if (response.isSuccess()) {
                //TODO: implement when success
            }
            return@withContext response
        }


    }

    override suspend fun prepopulateFetchInfo() {
        withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
            if (!fetchInfoDAO.existByAccountId(accountId)) accountId?.let { _accountId ->
                fetchInfoDAO.insert(FetchInfo(_accountId))
            }
        }
    }
}