package com.beeswork.balance.data.network.request

import org.threeten.bp.OffsetDateTime
import java.util.*


data class PostLocationBody(
    val accountId: UUID?,
    val identityToken: UUID?,
    val latitude: Double,
    val longitude: Double,
    val updatedAt: OffsetDateTime
)