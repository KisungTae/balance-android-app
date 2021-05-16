package com.beeswork.balance.ui.swipe

import java.util.*

data class CardDomain(
    val accountId: UUID,
    val name: String,
    val about: String,
    val height: Int?,
    var age: Int,
    val distance: Int,
    val photos: List<String>
)