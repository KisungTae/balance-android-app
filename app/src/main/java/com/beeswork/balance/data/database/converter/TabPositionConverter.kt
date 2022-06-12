package com.beeswork.balance.data.database.converter

import androidx.room.TypeConverter
import com.beeswork.balance.internal.constant.TabPosition

object TabPositionConverter {

    @TypeConverter
    @JvmStatic
    fun toTabPosition(ordinal: Int?): TabPosition? {
        return ordinal?.let {
            enumValues<TabPosition>()[ordinal]
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromTabPosition(paginationType: TabPosition?): Int? {
        return paginationType?.let {
            paginationType.ordinal
        }
    }
}