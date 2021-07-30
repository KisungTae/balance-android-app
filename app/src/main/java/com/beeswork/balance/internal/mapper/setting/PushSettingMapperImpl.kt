package com.beeswork.balance.internal.mapper.setting

import com.beeswork.balance.data.database.entity.PushSetting
import com.beeswork.balance.data.network.response.setting.PushSettingDTO
import com.beeswork.balance.ui.setting.push.PushSettingDomain
import java.util.*

class PushSettingMapperImpl : PushSettingMapper {

    override fun toPushSetting(accountId: UUID, pushSettingDTO: PushSettingDTO): PushSetting {
        return PushSetting(
            accountId,
            pushSettingDTO.matchPush,
            pushSettingDTO.clickedPush,
            pushSettingDTO.chatMessagePush,
            pushSettingDTO.emailPush,
            true
        )
    }

    override fun toPushSettingDomain(pushSetting: PushSetting): PushSettingDomain {
        return PushSettingDomain(
            pushSetting.matchPush,
            pushSetting.clickedPush,
            pushSetting.chatMessagePush,
            pushSetting.emailPush
        )
    }
}