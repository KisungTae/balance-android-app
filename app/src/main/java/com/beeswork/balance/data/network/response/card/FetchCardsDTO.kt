package com.beeswork.balance.data.network.response.card

data class FetchCardsDTO(
    val cardDTOs: MutableList<CardDTO>,
    val reset: Boolean
)