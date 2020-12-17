package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.pow

@Entity(tableName = "photo")
data class Photo(

    @PrimaryKey
    val key: String,

    val sequence: Long,
    var synced: Boolean,
) {
    companion object {

        const val MAX_SIZE = 1048576

        fun maxSizeInMB(): Int {
            val divider = 1024.0.pow(2.0)
            return (MAX_SIZE / divider).toInt()
        }
    }
}