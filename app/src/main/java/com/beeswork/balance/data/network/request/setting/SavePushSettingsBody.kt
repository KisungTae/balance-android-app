package com.beeswork.balance.data.network.request.setting

import java.util.*

data class SavePushSettingsBody(
    val accountId: UUID,
    val matchPush: Boolean,
    val clickedPush: Boolean,
    val chatMessagePush: Boolean,
    val emailPush: Boolean
)