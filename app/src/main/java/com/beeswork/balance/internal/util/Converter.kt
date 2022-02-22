package com.beeswork.balance.internal.util

import androidx.room.util.StringUtil
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern

class Converter {


    companion object {
        private val UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$")

        fun toUUID(uuid: String?): UUID? {
            if (uuid.isNullOrBlank()){
                return null
            }
            if (!UUID_REGEX_PATTERN.matcher(uuid).matches()) {
                return null
            }
            return try {
                UUID.fromString(uuid)
            } catch (e: Exception) {
                null
            }
        }
    }
}