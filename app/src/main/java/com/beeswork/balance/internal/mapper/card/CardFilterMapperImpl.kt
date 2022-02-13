package com.beeswork.balance.internal.mapper.card

import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.ui.cardfragment.filter.CardFilterDomain

class CardFilterMapperImpl : CardFilterMapper {
    override fun toCardFilterDomain(cardFilter: CardFilter): CardFilterDomain {
        return CardFilterDomain(cardFilter.gender, cardFilter.minAge, cardFilter.maxAge, cardFilter.distance)
    }
}