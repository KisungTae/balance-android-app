package com.beeswork.balance.data.provider

import android.content.Context
import androidx.preference.PreferenceManager
import com.beeswork.balance.internal.*
import com.beeswork.balance.internal.constant.PreferencesDefault
import com.beeswork.balance.internal.constant.PreferencesKey
import java.util.*

class PreferenceProviderImpl(
    context: Context
): PreferenceProvider {

    private val appContext = context.applicationContext
    private val preferences = PreferenceManager.getDefaultSharedPreferences(appContext)

    override fun getGender(): Boolean {
        return preferences.getBoolean(PreferencesKey.GENDER, PreferencesDefault.GENDER)
    }

    override fun getMinAgeBirthYear(): Int {
        val minAge = preferences.getFloat(PreferencesKey.MIN_AGE, PreferencesDefault.MIN_AGE)
        return Calendar.getInstance().get(Calendar.YEAR) - minAge.toInt()
    }

    override fun getMaxAgeBirthYear(): Int {
        val maxAge = preferences.getFloat(PreferencesKey.MAX_AGE, PreferencesDefault.MAX_AGE)
        return Calendar.getInstance().get(Calendar.YEAR) - maxAge.toInt()
    }

    override fun getDistance(): Int {
        return preferences.getFloat(PreferencesKey.DISTANCE, PreferencesDefault.DISTANCE).toInt() * 1000
    }

    override fun getAccountId(): String {
        return "456cb6c4-91e3-4b9d-8dda-062306154c6f"
    }

    override fun getEmail(): String {
        return "c0go233@gmail.com"
    }

    override fun getLatitude(): Double {
        return preferences.getDouble(PreferencesKey.LATITUDE, PreferencesDefault.LATITUDE)
    }

    override fun getLongitude(): Double {
        return preferences.getDouble(PreferencesKey.LONGITUDE, PreferencesDefault.LONGITUDE)
    }



}