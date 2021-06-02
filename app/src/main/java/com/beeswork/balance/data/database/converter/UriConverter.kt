package com.beeswork.balance.data.database.converter

import android.net.Uri
import androidx.room.TypeConverter

object UriConverter {

    @TypeConverter
    fun fromString(value: String?): Uri? {
        return value?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun toString(uri: Uri?): String? {
        return uri?.toString()
    }
}