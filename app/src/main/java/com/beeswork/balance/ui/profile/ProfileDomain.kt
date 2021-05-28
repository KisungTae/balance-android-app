package com.beeswork.balance.ui.profile

import org.threeten.bp.OffsetDateTime

data class ProfileDomain(
    val name: String,
    val birth: OffsetDateTime,
    val gender: Boolean,
    val height: Int?,
    val about: String
)