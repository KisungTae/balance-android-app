package com.beeswork.balance.data.database.converter

import androidx.room.TypeConverter
import com.beeswork.balance.internal.constant.Gender

object GenderConverter {

    @TypeConverter
    @JvmStatic
    fun toGender(ordinal: Int?): Gender? {
        return ordinal?.let {
            return enumValues<Gender>()[ordinal]
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromGender(gender: Gender?): Int? {
        return gender?.let {
            return gender.ordinal
        }
    }
}