package com.beeswork.balance.internal.converter

import androidx.room.TypeConverter
import com.beeswork.balance.data.database.entity.ChatMessage

object MessageStatusConverter {

    @TypeConverter
    @JvmStatic
    fun toStatus(ordinal: Int?): ChatMessage.Status? {
        return ordinal?.let {
            return enumValues<ChatMessage.Status>()[ordinal]
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromStatus(status: ChatMessage.Status?): Int? {
        return status?.let {
            return status.ordinal
        }
    }
}