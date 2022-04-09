package com.beeswork.balance.data.database.result

import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.internal.constant.ClickOutcome

data class ClickResult(
    val clickOutcome: ClickOutcome,
    val match: Match?
)