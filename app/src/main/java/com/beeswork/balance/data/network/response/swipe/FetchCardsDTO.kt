package com.beeswork.balance.data.network.response.swipe

data class FetchCardsDTO(
    val cardDTOs: MutableList<CardDTO>,
    val reset: Boolean
)