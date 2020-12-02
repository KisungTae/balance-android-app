package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "photo")
data class Photo(

    @PrimaryKey
    val key: String,

    val sequence: Long,
    var synced: Boolean,
)