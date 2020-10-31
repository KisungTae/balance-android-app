package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


@Entity(tableName = "click")
data class Click(

    @PrimaryKey
    val swipedId: String
)