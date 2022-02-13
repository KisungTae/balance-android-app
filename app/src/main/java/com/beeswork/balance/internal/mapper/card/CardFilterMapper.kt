package com.beeswork.balance.internal.mapper.card

import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.ui.cardfragment.filter.CardFilterDomain

interface CardFilterMapper {
    fun toCardFilterDomain(cardFilter: CardFilter): CardFilterDomain
}