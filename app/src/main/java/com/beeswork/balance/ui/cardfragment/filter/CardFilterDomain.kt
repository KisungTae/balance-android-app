package com.beeswork.balance.ui.cardfragment.filter

data class CardFilterDomain(
    val gender: Boolean,
    val minAge: Int,
    val maxAge: Int,
    val distance: Int
)