package com.beeswork.balance.data.database.tuple

data class PushSettingsTuple(
    val matchPush: Boolean,
    val clickedPush: Boolean,
    val chatMessagePush: Boolean
)