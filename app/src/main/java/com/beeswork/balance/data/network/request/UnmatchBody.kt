package com.beeswork.balance.data.network.request

import java.util.*

data class UnmatchBody(
    val accountId: UUID?,
    val identityToken: UUID?,
    val unmatchedId: UUID
)