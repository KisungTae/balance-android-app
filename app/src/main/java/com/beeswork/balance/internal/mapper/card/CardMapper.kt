package com.beeswork.balance.internal.mapper.card

import com.beeswork.balance.data.database.entity.card.Card
import com.beeswork.balance.data.network.response.card.CardDTO
import com.beeswork.balance.domain.uistate.card.CardItemUIState

interface CardMapper {
    fun toCardItemUIState(card: Card, photoDomain: String?): CardItemUIState
    fun toCard(cardDTO: CardDTO): Card
}