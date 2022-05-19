package com.beeswork.balance.domain.uistate.profile

import org.threeten.bp.LocalDate

data class ProfileUIState(
    val name: String?,
    val birth: LocalDate?,
    val gender: Boolean?,
    val height: Int?,
    val about: String?,
    val age: Int?
)