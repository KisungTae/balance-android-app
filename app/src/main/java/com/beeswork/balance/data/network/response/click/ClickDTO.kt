package com.beeswork.balance.data.network.response.click

import org.threeten.bp.OffsetDateTime
import java.util.*

data class ClickDTO(
    val swiperId: UUID,
    val name: String,
    val deleted: Boolean,
    val profilePhotoKey: String,
    val updatedAt: OffsetDateTime
)