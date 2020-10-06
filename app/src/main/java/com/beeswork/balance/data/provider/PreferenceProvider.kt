package com.beeswork.balance.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


interface PreferenceProvider {

    fun getGender(): Boolean
    fun getMinAgeBirthYear(): Int
    fun getMaxAgeBirthYear(): Int
    fun getDistance(): Int
    fun getAccountId(): String
    fun getLatitude(): Double
    fun getLongitude(): Double
    fun getEmail(): String
}