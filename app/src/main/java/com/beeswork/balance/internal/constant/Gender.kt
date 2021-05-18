package com.beeswork.balance.internal.constant

import com.beeswork.balance.internal.util.safeLet
import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type


enum class Gender {

    @SerializedName("0")
    MALE,

    @SerializedName("1")
    FEMALE;
}