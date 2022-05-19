package com.beeswork.balance.internal.provider.gson

import com.google.gson.*
import org.threeten.bp.LocalDate
import java.lang.Exception
import java.lang.reflect.Type

class LocalDateToStringSerializer: JsonSerializer<LocalDate> {
    override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return try {
            JsonPrimitive(src.toString())
        } catch (e: Exception) {
            null
        }
    }
}