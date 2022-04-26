package com.beeswork.balance.data.network.response.card

data class FetchCardsResponse(
    val cardDTOs: List<CardDTO>,
    val reset: Boolean
)