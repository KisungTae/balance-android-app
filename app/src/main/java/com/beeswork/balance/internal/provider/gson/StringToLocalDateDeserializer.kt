package com.beeswork.balance.internal.provider.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.threeten.bp.LocalDate
import java.lang.reflect.Type

class StringToLocalDateDeserializer: JsonDeserializer<LocalDate> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate? {
        return json?.asJsonPrimitive?.asString?.let { jsonValue ->
            LocalDate.parse(jsonValue)
        }
    }
}