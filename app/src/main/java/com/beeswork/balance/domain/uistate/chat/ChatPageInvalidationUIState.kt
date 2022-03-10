package com.beeswork.balance.domain.uistate.chat

import com.beeswork.balance.internal.constant.ChatMessageStatus

data class ChatPageInvalidationUIState(
    val scrollToBottom: Boolean,
    val body: String?,
)