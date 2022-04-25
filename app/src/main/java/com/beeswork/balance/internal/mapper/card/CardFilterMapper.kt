package com.beeswork.balance.internal.mapper.card

import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.domain.uistate.card.CardFilterUIState

interface CardFilterMapper {
    fun toCardFilterUIState(cardFilter: CardFilter): CardFilterUIState
}