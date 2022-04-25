package com.beeswork.balance.domain.uistate.card

data class CardFilterUIState(
    val gender: Boolean?,
    val minAge: Int,
    val maxAge: Int,
    val distance: Int
)