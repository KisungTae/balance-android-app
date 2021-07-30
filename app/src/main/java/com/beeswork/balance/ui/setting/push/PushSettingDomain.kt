package com.beeswork.balance.ui.setting.push

data class PushSettingDomain(
    val matchPush: Boolean,
    val clickedPush: Boolean,
    val chatMessagePush: Boolean,
    val emailPush: Boolean
)