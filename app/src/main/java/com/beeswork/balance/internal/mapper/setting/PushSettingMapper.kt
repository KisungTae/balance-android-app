package com.beeswork.balance.internal.mapper.setting

import com.beeswork.balance.data.database.entity.setting.PushSetting
import com.beeswork.balance.data.network.response.setting.PushSettingDTO
import com.beeswork.balance.ui.setting.push.PushSettingDomain
import java.util.*

interface PushSettingMapper {
    fun toPushSetting(accountId: UUID, pushSettingDTO: PushSettingDTO): PushSetting
    fun toPushSettingDomain(pushSetting: PushSetting): PushSettingDomain
}