package com.beeswork.balance.data.network.response.click

import org.threeten.bp.OffsetDateTime
import java.util.*

data class ClickDTO(
    val id: Long,
    val swiperId: UUID,
    var swipedId: UUID?,
    val name: String?,
    val clicked: Boolean,
    val deleted: Boolean,
    val profilePhotoKey: String?,
)