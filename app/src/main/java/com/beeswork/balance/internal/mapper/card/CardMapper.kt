package com.beeswork.balance.internal.mapper.card

import com.beeswork.balance.ui.cardfragment.card.CardDomain

interface CardMapper {
    fun toCardDomain(cardDTO: com.beeswork.balance.data.network.response.card.CardDTO): CardDomain
}