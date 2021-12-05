package com.beeswork.balance.data.database.converter

import androidx.room.TypeConverter
import com.beeswork.balance.internal.constant.ChatMessageStatus

object ChatMessageStatusConverter {

    @TypeConverter
    @JvmStatic
    fun toStatus(ordinal: Int?): ChatMessageStatus? {
        return ordinal?.let {
            return enumValues<ChatMessageStatus>()[ordinal]
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromStatus(status: ChatMessageStatus?): Int? {
        return status?.let {
            return status.ordinal
        }
    }
}