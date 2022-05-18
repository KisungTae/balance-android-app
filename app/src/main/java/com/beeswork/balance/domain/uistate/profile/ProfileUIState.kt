package com.beeswork.balance.domain.uistate.profile

import org.threeten.bp.OffsetDateTime

data class ProfileUIState(
    val name: String?,
    val birth: OffsetDateTime?,
    val gender: Boolean?,
    val height: Int?,
    val about: String?,
    val age: Int?
)