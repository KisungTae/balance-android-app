package com.beeswork.balance.domain.usecase.card

import com.beeswork.balance.data.database.entity.card.CardFilter

interface GetCardFilterUseCase {

    suspend fun invoke(): CardFilter?
}