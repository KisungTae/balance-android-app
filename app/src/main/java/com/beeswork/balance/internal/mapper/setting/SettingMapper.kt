package com.beeswork.balance.internal.mapper.setting

import com.beeswork.balance.data.database.entity.Setting
import com.beeswork.balance.data.network.response.setting.SettingDTO
import com.beeswork.balance.ui.setting.SettingDomain
import java.util.*

interface SettingMapper {
    fun toSetting(accountId: UUID, settingDTO: SettingDTO, synced: Boolean): Setting
}