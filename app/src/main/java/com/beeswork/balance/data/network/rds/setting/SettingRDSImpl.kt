package com.beeswork.balance.data.network.rds.setting

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.setting.SaveFCMTokenBody
import com.beeswork.balance.data.network.request.setting.SaveLocationBody
import com.beeswork.balance.data.network.request.setting.SavePushSettingsBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.setting.PushSettingDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import org.threeten.bp.OffsetDateTime

class SettingRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), SettingRDS {

    override suspend fun fetchPushSetting(): Resource<PushSettingDTO> {
        return getResult { balanceAPI.getPushSetting() }
    }

    override suspend fun deleteAccount(): Resource<EmptyResponse> {
        return getResult { balanceAPI.deleteAccount() }
    }

    override suspend fun savePushSettings(
        matchPush: Boolean,
        clickedPush: Boolean,
        chatMessagePush: Boolean,
        emailPush: Boolean
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.savePushSettings(SavePushSettingsBody(matchPush, clickedPush, chatMessagePush, emailPush))
        }
    }

    override suspend fun saveFCMToken(token: String): Resource<EmptyResponse> {
        return getResult { balanceAPI.saveFCMToken(SaveFCMTokenBody(token)) }
    }

    override suspend fun saveLocation(
        latitude: Double,
        longitude: Double,
        updatedAt: OffsetDateTime
    ): Resource<EmptyResponse> {
        return getResult { balanceAPI.saveLocation(SaveLocationBody(latitude, longitude, updatedAt)) }
    }

}