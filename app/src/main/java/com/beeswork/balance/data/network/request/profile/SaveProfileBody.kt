package com.beeswork.balance.data.network.request.profile

import org.threeten.bp.OffsetDateTime

data class SaveProfileBody(
    val name: String,
    val gender: Boolean,
    val birthDate: OffsetDateTime,
    val height: Int?,
    val about: String?,
    val latitude: Double,
    val longitude: Double
)