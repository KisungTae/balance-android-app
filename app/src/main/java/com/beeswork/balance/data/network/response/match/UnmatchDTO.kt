package com.beeswork.balance.data.network.response.match

import org.threeten.bp.OffsetDateTime

data class UnmatchDTO(
    val matchCount: Long,
    val matchCountCountedAt: OffsetDateTime
)