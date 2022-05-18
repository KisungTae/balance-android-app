package com.beeswork.balance.internal.provider.gson

import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.threeten.bp.LocalDate
import java.lang.reflect.Type

class LocalDateToStringSerializer: JsonSerializer<LocalDate> {
    override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        TODO("Not yet implemented")
    }
}