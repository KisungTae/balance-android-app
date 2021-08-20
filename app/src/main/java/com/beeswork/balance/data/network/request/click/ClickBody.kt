package com.beeswork.balance.data.network.request.click

import retrofit2.http.FieldMap
import java.util.*

data class ClickBody(
    val accountId: UUID,
    val swipedId: UUID,
    val answers: Map<Int, Boolean>
)