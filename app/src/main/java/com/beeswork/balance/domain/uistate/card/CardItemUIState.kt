package com.beeswork.balance.domain.uistate.card

import java.util.*

data class CardItemUIState(
    val accountId: UUID,
    val name: String,
    val about: String?,
    val height: Int?,
    val age: Int,
    val distance: Int,
    val photoURLs: List<String>
)