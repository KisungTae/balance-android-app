package com.beeswork.balance.ui.swipe.card

import java.util.*

data class CardDomain(
    val accountId: UUID,
    val name: String,
    val about: String?,
    val height: Int?,
    val age: Int,
    val distance: Int,
    val photoKeys: List<String>
)