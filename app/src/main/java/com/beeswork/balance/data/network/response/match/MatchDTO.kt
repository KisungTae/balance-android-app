package com.beeswork.balance.data.network.response.match

import com.beeswork.balance.internal.constant.PushType
import org.threeten.bp.OffsetDateTime
import java.util.*

data class MatchDTO(
    val pushType: PushType,
    val chatId: Long,
    val swiperId: UUID?,
    val swipedId: UUID,
    val active: Boolean,
    val unmatched: Boolean,
    val name: String,
    val profilePhotoKey: String?,
    val deleted: Boolean,
    val createdAt: OffsetDateTime?
)