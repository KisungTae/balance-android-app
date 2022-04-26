package com.beeswork.balance.internal.mapper.card

import com.beeswork.balance.data.database.entity.card.Card
import com.beeswork.balance.data.network.response.card.CardDTO
import com.beeswork.balance.domain.uistate.card.CardItemUIState
import java.util.*

class CardMapperImpl : CardMapper {

    override fun toCardItemUIState(card: Card): CardItemUIState {
        return CardItemUIState(
            card.accountId,
            card.name,
            card.about,
            card.height,
            getAgeFromBirthYear(card.birthYear),
            card.distance,
            card.photoKeys
        )
    }

    override fun toCard(cardDTO: CardDTO): Card {
        return Card(
            cardDTO.accountId,
            cardDTO.name,
            cardDTO.about,
            cardDTO.height,
            cardDTO.birthYear,
            cardDTO.distance,
            cardDTO.photoKeys
        )
    }

    private fun getAgeFromBirthYear(birthYear: Int): Int {
        return Calendar.getInstance().get(Calendar.YEAR) - birthYear + 1
    }

}