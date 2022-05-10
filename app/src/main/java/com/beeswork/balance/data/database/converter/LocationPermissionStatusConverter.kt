package com.beeswork.balance.data.database.converter

import androidx.room.TypeConverter
import com.beeswork.balance.internal.constant.LocationPermissionStatus

object LocationPermissionStatusConverter {

    @TypeConverter
    @JvmStatic
    fun toStatus(ordinal: Int?): LocationPermissionStatus? {
        return ordinal?.let {
            return enumValues<LocationPermissionStatus>()[ordinal]
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromStatus(status: LocationPermissionStatus?): Int? {
        return status?.let {
            return status.ordinal
        }
    }
}