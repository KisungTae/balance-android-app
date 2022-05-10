package com.beeswork.balance.internal.mapper.card

import com.beeswork.balance.data.database.entity.card.Card
import com.beeswork.balance.data.network.response.card.CardDTO
import com.beeswork.balance.domain.uistate.card.CardItemUIState
import com.beeswork.balance.internal.constant.EndPoint
import java.util.*

class CardMapperImpl : CardMapper {

    override fun toCardItemUIState(card: Card, photoDomain: String?): CardItemUIState {
        val photoURLs = card.photoKeys.map { photoKey ->
            EndPoint.ofPhoto(photoDomain, card.accountId, photoKey)
        }
        return CardItemUIState(
            card.accountId,
            card.name,
            card.gender,
            card.about,
            card.height,
            getAgeFromBirthYear(card.birthYear),
            card.distance,
            photoURLs
        )
    }

    override fun toCard(cardDTO: CardDTO): Card {
        return Card(
            cardDTO.accountId,
            cardDTO.name,
            cardDTO.gender,
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