package com.beeswork.balance.internal.mapper.setting

import com.beeswork.balance.data.database.entity.Setting
import com.beeswork.balance.data.network.response.setting.SettingDTO
import java.util.*

class SettingMapperImpl : SettingMapper {
    override fun toSetting(accountId: UUID, settingDTO: SettingDTO, synced: Boolean): Setting {
        return Setting(
            accountId,
            settingDTO.matchPush,
            settingDTO.clickedPush,
            settingDTO.chatMessagePush,
            settingDTO.emailPush,
            synced
        )
    }
}