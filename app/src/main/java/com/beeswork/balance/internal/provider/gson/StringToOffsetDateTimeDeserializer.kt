package com.beeswork.balance.internal.provider.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.Exception
import java.lang.reflect.Type

class StringToOffsetDateTimeDeserializer: JsonDeserializer<OffsetDateTime> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): OffsetDateTime? {
        return try {
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(json?.asString, OffsetDateTime::from)
        } catch (e: Exception) {
            null
        }
    }
}