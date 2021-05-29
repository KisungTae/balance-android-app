package com.beeswork.balance.data.network.request

import java.util.*

data class SaveAnswersBody(
    val accountId: UUID?,
    val identityToken: UUID?,
    val answers: Map<Int, Boolean>
)