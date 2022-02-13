package com.beeswork.balance.ui.settingfragment.push

data class PushSettingDomain(
    val matchPush: Boolean,
    val swipePush: Boolean,
    val chatMessagePush: Boolean,
    val emailPush: Boolean
)