package com.beeswork.balance.data.network.request

import java.util.*

data class PostPushSettingsBody(
    val accountId: UUID,
    val identityToken: UUID,
    val matchPush: Boolean,
    val clickedPush: Boolean,
    val chatMessagePush: Boolean,
    val emailPush: Boolean
)