package com.beeswork.balance.data.database.entity.match

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*

@Entity(tableName = "matchCount")
data class MatchCount(

    @PrimaryKey
    val accountId: UUID,

    var count: Long,
    var countedAt: OffsetDateTime = OffsetDateTime.MIN
)