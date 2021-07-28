package com.beeswork.balance.internal.constant

import com.beeswork.balance.internal.util.safeLet
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type


@JsonAdapter(LoginType.Serializer::class)
enum class LoginType {
    NAVER,
    KAKAO,
    GOOGLE,
    FACEBOOK;

    fun isEmailEditable(): Boolean {
        return this == KAKAO || this == FACEBOOK
    }

    internal class Serializer : JsonSerializer<LoginType?> {
        override fun serialize(src: LoginType?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
            return safeLet(src, context) { s, c ->
                c.serialize(s.ordinal)
            }
        }
    }
}