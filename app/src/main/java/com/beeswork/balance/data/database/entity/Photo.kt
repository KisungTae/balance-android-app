package com.beeswork.balance.data.database.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.PhotoStatus
import java.util.*
import kotlin.math.pow

@Entity(tableName = "photo")
data class Photo(

    @PrimaryKey
    val key: UUID,

    var status: PhotoStatus,
    var uri: Uri?,
    var sequence: Int,
    var oldSequence: Int,
    var photoUploaded: Boolean,
    var photoCreated: Boolean,
)