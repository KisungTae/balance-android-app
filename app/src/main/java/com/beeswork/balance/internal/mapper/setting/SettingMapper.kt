package com.beeswork.balance.internal.mapper.setting

import com.beeswork.balance.data.database.entity.Setting
import com.beeswork.balance.ui.settings.SettingDomain

interface SettingMapper {

    fun toSettingDomain(setting: Setting): SettingDomain {
        return SettingDomain(setting.email)
    }
}