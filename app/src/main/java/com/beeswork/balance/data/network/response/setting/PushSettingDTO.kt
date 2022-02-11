package com.beeswork.balance.data.network.response.setting

data class PushSettingDTO(
    val matchPush: Boolean,
    val swipePush: Boolean,
    val chatMessagePush: Boolean,
    val emailPush: Boolean
)