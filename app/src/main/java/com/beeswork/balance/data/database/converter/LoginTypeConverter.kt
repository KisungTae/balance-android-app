package com.beeswork.balance.data.database.converter

import androidx.room.TypeConverter
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.constant.LoginType

object LoginTypeConverter {

    @TypeConverter
    @JvmStatic
    fun toStatus(ordinal: Int?): LoginType? {
        return ordinal?.let {
            return enumValues<LoginType>()[ordinal]
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromStatus(status: LoginType?): Int? {
        return status?.let {
            return status.ordinal
        }
    }
}