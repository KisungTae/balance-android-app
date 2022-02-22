package com.beeswork.balance.data.database.entity.swipe

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*

@Entity(tableName = "swipeCount")
data class SwipeCount(

    @PrimaryKey
    val accountId: UUID,

    var count: Long,
    var countedAt: OffsetDateTime = OffsetDateTime.MIN
)