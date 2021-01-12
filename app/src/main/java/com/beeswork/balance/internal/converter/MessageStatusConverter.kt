package com.beeswork.balance.internal.converter

import androidx.room.TypeConverter
import com.beeswork.balance.data.database.entity.Message

object MessageStatusConverter {

    @TypeConverter
    @JvmStatic
    fun toStatus(ordinal: Int?): Message.Status? {
        return ordinal?.let {
            return enumValues<Message.Status>()[ordinal]
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromStatus(status: Message.Status?): Int? {
        return status?.let {
            return status.ordinal
        }
    }
}