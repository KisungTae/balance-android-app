package com.beeswork.balance.data.network.rds.setting

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.setting.DeleteAccountBody
import com.beeswork.balance.data.network.request.setting.SaveFCMTokenBody
import com.beeswork.balance.data.network.request.setting.SaveLocationBody
import com.beeswork.balance.data.network.request.setting.SavePushSettingsBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.setting.PushSettingDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import org.threeten.bp.OffsetDateTime
import java.util.*

class SettingRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), SettingRDS {

    override suspend fun fetchPushSetting(accountId: UUID): Resource<PushSettingDTO> {
        return getResult { balanceAPI.getPushSetting(accountId) }
    }

    override suspend fun deleteAccount(accountId: UUID): Resource<EmptyResponse> {
        return getResult { balanceAPI.deleteAccount(DeleteAccountBody(accountId)) }
    }

    override suspend fun savePushSettings(
        accountId: UUID,
        matchPush: Boolean,
        clickedPush: Boolean,
        chatMessagePush: Boolean,
        emailPush: Boolean
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.savePushSettings(
                SavePushSettingsBody(
                    accountId,
                    matchPush,
                    clickedPush,
                    chatMessagePush,
                    emailPush
                )
            )
        }
    }

    override suspend fun saveFCMToken(accountId: UUID, token: String): Resource<EmptyResponse> {
        return getResult { balanceAPI.saveFCMToken(SaveFCMTokenBody(accountId, token)) }
    }

    override suspend fun saveLocation(
        accountId: UUID,
        latitude: Double,
        longitude: Double,
        updatedAt: OffsetDateTime
    ): Resource<EmptyResponse> {
        return getResult { balanceAPI.saveLocation(SaveLocationBody(accountId, latitude, longitude, updatedAt)) }
    }

}