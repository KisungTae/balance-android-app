package com.beeswork.balance.internal.mapper.swipe

import com.beeswork.balance.data.network.response.swipe.CardDTO
import com.beeswork.balance.ui.swipe.CardDomain
import java.util.*

class CardMapperImpl : CardMapper {

    override fun toCardDomain(cardDTO: CardDTO): CardDomain {
        return CardDomain(
            cardDTO.accountId,
            cardDTO.name,
            cardDTO.about,
            cardDTO.height,
            getAgeFromBirthYear(cardDTO.birthYear),
            cardDTO.distance,
            cardDTO.photoKeys
        )
    }

    private fun getAgeFromBirthYear(birthYear: Int): Int {
        return Calendar.getInstance().get(Calendar.YEAR) - birthYear + 1
    }

}