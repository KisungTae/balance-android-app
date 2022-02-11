package com.beeswork.balance.data.network.request.setting

import java.util.*

data class SavePushSettingsBody(
    val matchPush: Boolean,
    val swipePush: Boolean,
    val chatMessagePush: Boolean,
    val emailPush: Boolean
)