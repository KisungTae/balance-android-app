package com.beeswork.balance.data.database.converter

import androidx.room.TypeConverter
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.constant.PhotoStatus

object PhotoStatusConverter {

    @TypeConverter
    @JvmStatic
    fun toStatus(ordinal: Int?): PhotoStatus? {
        return ordinal?.let {
            return enumValues<PhotoStatus>()[ordinal]
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromStatus(status: PhotoStatus?): Int? {
        return status?.let {
            return status.ordinal
        }
    }
}