package com.beeswork.balance.data.network.response

data class Card (
    val accountId: String,
    val name: String,
    val about: String,
    var birthYear: Int,
    val distance: Int,
    val photos: List<String>
)