package com.beeswork.balance.data.network.response

data class CardResponse (
    val accountId: String,
    val name: String,
    val about: String,
    val height: Int?,
    var birthYear: Int,
    val distance: Int,
    val photos: List<String>
)