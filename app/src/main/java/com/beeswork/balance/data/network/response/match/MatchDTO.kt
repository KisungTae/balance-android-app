package com.beeswork.balance.data.network.response.match

import com.beeswork.balance.internal.constant.PushType
import org.threeten.bp.OffsetDateTime
import java.util.*

data class MatchDTO(
    val chatId: Long,
    val swiperId: UUID?,
    val swipedId: UUID,
    val active: Boolean,
    val unmatched: Boolean,
    val delete: Boolean,
    val swipedName: String,
    val swipedProfilePhotoKey: String?,
    val swipedDeleted: Boolean
)