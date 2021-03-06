package com.beeswork.balance.data.database.converter

import androidx.room.TypeConverter
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ChatMessageStatus

object ResourceStatusConverter {

    @TypeConverter
    @JvmStatic
    fun toStatus(ordinal: Int?): Resource.Status? {
        return ordinal?.let {
            return enumValues<Resource.Status>()[ordinal]
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromStatus(status: Resource.Status?): Int? {
        return status?.let {
            return status.ordinal
        }
    }
}