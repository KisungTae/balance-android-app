package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*

@Entity(tableName = "fetchInfo")
data class FetchInfo(

    @PrimaryKey
    val accountId: UUID,

    val clickFetchedAt: OffsetDateTime,
    val matchFetchedAt: OffsetDateTime
)