package com.beeswork.balance.internal.mapper.card

import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.domain.uistate.card.CardFilterUIState

class CardFilterMapperImpl : CardFilterMapper {
    override fun toCardFilterUIState(cardFilter: CardFilter): CardFilterUIState {
        return CardFilterUIState(cardFilter.gender, cardFilter.minAge, cardFilter.maxAge, cardFilter.distance)
    }
}