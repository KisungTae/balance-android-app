package com.beeswork.balance.internal.mapper.swipe

import com.beeswork.balance.data.network.response.swipe.CardDTO
import com.beeswork.balance.ui.swipe.CardDomain

interface CardMapper {
    fun toCardDomain(cardDTO: CardDTO): CardDomain
}