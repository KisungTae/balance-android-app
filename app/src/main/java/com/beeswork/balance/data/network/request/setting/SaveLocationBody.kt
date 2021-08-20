package com.beeswork.balance.data.network.request.setting

import org.threeten.bp.OffsetDateTime
import java.util.*


data class SaveLocationBody(
    val accountId: UUID,
    val latitude: Double,
    val longitude: Double,
    val updatedAt: OffsetDateTime
)