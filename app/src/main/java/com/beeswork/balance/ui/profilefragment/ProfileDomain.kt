package com.beeswork.balance.ui.profilefragment

import org.threeten.bp.OffsetDateTime

data class ProfileDomain(
    val name: String?,
    val birth: OffsetDateTime?,
    val gender: Boolean?,
    val height: Int?,
    val about: String?
)