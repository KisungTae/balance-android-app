package com.beeswork.balance.internal.mapper.swipe

import com.beeswork.balance.ui.swipe.card.CardDomain

interface CardMapper {
    fun toCardDomain(cardDTO: com.beeswork.balance.data.network.response.swipe.CardDTO): CardDomain
}