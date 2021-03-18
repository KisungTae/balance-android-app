package com.beeswork.balance.data.database.converter

import androidx.room.TypeConverter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

object OffsetDateTimeConverter {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {

        return date?.format(formatter)
    }

    fun toOffsetDateTimeNonNull(value: String): OffsetDateTime {
        return formatter.parse(value, OffsetDateTime::from)
    }

    fun fromOffsetDateTimeNonNull(date: OffsetDateTime): String {
        return date.format(formatter)
    }




}