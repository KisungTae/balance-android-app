package com.beeswork.balance.data.network.response.profile

import org.threeten.bp.OffsetDateTime
import java.util.*

data class ProfileDTO(
    val name: String,
    val birthDate: OffsetDateTime,
    val gender: Boolean,
    val height: Int?,
    val about: String?
)