package com.beeswork.balance.internal.constant

import com.beeswork.balance.internal.util.safeLet
import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type


class Gender {
    companion object {
        const val FEMALE = false
        const val MALE = true

        fun getOppositeGender(gender: Boolean): Boolean {
            return if (gender == FEMALE) MALE
            else FEMALE
        }
    }
}