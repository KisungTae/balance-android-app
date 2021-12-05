package com.beeswork.balance.ui.swipe.filter

data class SwipeFilterDomain(
    val gender: Boolean,
    val minAge: Int,
    val maxAge: Int,
    val distance: Int
)