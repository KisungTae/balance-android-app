package com.beeswork.balance.ui.matchfragment

import org.threeten.bp.ZonedDateTime
import java.util.*

data class MatchItemUIState(
    val chatId: UUID,
    val swipedId: UUID,
    val active: Boolean,
    val unread: Boolean,
    val unmatched: Boolean,
    val lastChatMessageBody: String,
    val swipedName: String?,
    val swipedProfilePhotoUrl: String?,
)