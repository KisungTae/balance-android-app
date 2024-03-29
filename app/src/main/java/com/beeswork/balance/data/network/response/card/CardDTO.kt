package com.beeswork.balance.data.network.response.card

import java.util.*

data class CardDTO (
    val accountId: UUID,
    val name: String,
    val gender: Boolean,
    val about: String?,
    val height: Int?,
    val birthYear: Int,
    val distance: Int,
    val photoKeys: List<String> = arrayListOf()
)