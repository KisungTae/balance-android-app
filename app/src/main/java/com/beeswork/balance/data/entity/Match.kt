package com.beeswork.balance.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


@Entity(tableName = "match",
        indices = [Index(value = ["matchedId"], unique = true)])
data class Match(

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    val matchedId: String,
    val photoKey: String,
    val name: String,
    val unmatched: Boolean,
    val recentMessage: String,
    val lastRead: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null
)