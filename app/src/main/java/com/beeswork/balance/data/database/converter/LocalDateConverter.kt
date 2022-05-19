package com.beeswork.balance.data.database.converter

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate

object LocalDateConverter {

    @TypeConverter
    @JvmStatic
    fun toString(localDate: LocalDate?): String? {
        return localDate?.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { _value -> LocalDate.parse(_value) }
    }
}