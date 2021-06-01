package com.beeswork.balance.data.database.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.PhotoStatus
import kotlin.math.pow

@Entity(tableName = "photo")
data class Photo(

    @PrimaryKey
    val key: String,

    var status: PhotoStatus,
    var uriPath: String?,
    var sequence: Int,
    var synced: Boolean
) {
    companion object {

        const val MAX_SIZE = 1048576

        fun maxSizeInMB(): Int {
            val divider = 1024.0.pow(2.0)
            return (MAX_SIZE / divider).toInt()
        }
    }
}