package com.beeswork.balance.data.network.response.setting

data class SettingDTO(
    val matchPush: Boolean,
    val clickedPush: Boolean,
    val chatMessagePush: Boolean,
    val emailPush: Boolean
)