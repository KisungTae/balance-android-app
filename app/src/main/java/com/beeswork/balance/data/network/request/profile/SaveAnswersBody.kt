package com.beeswork.balance.data.network.request.profile

import java.util.*

data class SaveAnswersBody(
    val answers: Map<Int, Boolean>
)