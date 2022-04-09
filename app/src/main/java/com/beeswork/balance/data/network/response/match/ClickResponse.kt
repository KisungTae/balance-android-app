package com.beeswork.balance.data.network.response.match

import com.beeswork.balance.internal.constant.ClickOutcome

data class ClickResponse(
    val clickOutcome: ClickOutcome,
    val matchDTO: MatchDTO?
)