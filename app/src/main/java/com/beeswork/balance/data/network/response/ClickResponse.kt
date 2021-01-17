package com.beeswork.balance.data.network.response

import com.beeswork.balance.data.database.entity.Match

data class ClickResponse(
    val result: String,
    val match: Match
)