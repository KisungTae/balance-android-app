package com.beeswork.balance.data.network.response.match

import com.beeswork.balance.internal.constant.ClickResult

data class ClickDTO(
    val clickResult: ClickResult,
    val matchDTO: MatchDTO?
)