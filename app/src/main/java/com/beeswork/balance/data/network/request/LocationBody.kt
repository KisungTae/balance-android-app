package com.beeswork.balance.data.network.request

import org.threeten.bp.OffsetDateTime


data class LocationBody(
    val accountId: String,
    val identityToken: String,
    val latitude: Double,
    val longitude: Double,
    val updatedAt: String
)