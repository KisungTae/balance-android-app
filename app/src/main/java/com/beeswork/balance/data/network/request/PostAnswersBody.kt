package com.beeswork.balance.data.network.request

import java.util.*

data class PostAnswersBody(
    val accountId: UUID?,
    val identityToken: UUID?,
    val answers: Map<Int, Boolean>
)