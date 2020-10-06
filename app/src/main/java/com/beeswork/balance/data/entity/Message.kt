package com.beeswork.balance.data.entity

import androidx.room.*
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "message",
        foreignKeys = [ForeignKey(entity = Match::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("matchId"),
            onDelete = ForeignKey.CASCADE)],
        indices = [Index(value = ["matchId"])])
data class Message(

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    val matchId: Long,
    val isReceived: Boolean,
    val message: String,
    val createdAt: OffsetDateTime? = null
)