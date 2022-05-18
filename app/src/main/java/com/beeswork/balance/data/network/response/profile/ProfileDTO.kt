package com.beeswork.balance.data.network.response.profile

import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import java.util.*

data class ProfileDTO(
    val accountId: UUID,
    val name: String,
    val birthDate: LocalDate,
    val gender: Boolean,
    val height: Int?,
    val about: String?
)