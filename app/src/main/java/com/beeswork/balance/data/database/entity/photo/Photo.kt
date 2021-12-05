package com.beeswork.balance.data.database.entity.photo

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.PhotoStatus
import java.util.*
import kotlin.math.pow

@Entity(tableName = "photo")
data class Photo(

    @PrimaryKey
    val key: String,

    val accountId: UUID,
    var status: PhotoStatus,
    var uri: Uri?,
    var sequence: Int,
    var oldSequence: Int,
    var uploaded: Boolean,
    var saved: Boolean
)