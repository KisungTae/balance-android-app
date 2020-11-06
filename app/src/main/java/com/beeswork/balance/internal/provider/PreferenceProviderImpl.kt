package com.beeswork.balance.internal.provider

import android.content.Context
import androidx.preference.PreferenceManager
import com.beeswork.balance.internal.*
import com.beeswork.balance.internal.constant.PreferencesDefault
import com.beeswork.balance.internal.constant.PreferencesKey
import org.threeten.bp.OffsetDateTime
import java.util.*

class PreferenceProviderImpl(
    context: Context
): PreferenceProvider {

    private val appContext = context.applicationContext
    private val preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    private val editor = preferences.edit()

    override fun putSwipeFilterValues(
        gender: Boolean,
        minAge: Float,
        maxAge: Float,
        distance: Float
    ) {
        editor.putBoolean(PreferencesKey.GENDER, gender)
        editor.putFloat(PreferencesKey.MIN_AGE, minAge)
        editor.putFloat(PreferencesKey.MAX_AGE, maxAge)
        editor.putFloat(PreferencesKey.DISTANCE, distance)
        editor.apply()
    }

    override fun putLocation(latitude: Double, longitude: Double) {
        editor.putDouble(PreferencesKey.LATITUDE, latitude)
        editor.putDouble(PreferencesKey.LONGITUDE, longitude)
        editor.apply()
    }

    override fun putAccountId(accountId: String) {
    }

    override fun putEmail(email: String) {
    }

    override fun putMatchFetchedAt(matchFetchedAt: String) {
        editor.putString(PreferencesKey.MATCH_FETCHED_AT, matchFetchedAt)
    }

    override fun putClickedFetchedAt(clickedFetchedAt: String) {
        editor.putString(PreferencesKey.CLICKED_FETCHED_AT, clickedFetchedAt)
    }






    override fun getClickedFetchedAt(): String {
        return preferences.getString(PreferencesKey.CLICKED_FETCHED_AT, "2020-01-01T10:06:26.032Z")!!
    }


    override fun getMatchFetchedAt(): String {
        return preferences.getString(PreferencesKey.MATCH_FETCHED_AT, "2020-01-01T10:06:26.032Z")!!
    }

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

    override fun getMinAge(): Float {
        return preferences.getFloat(PreferencesKey.MIN_AGE, PreferencesDefault.MIN_AGE)
    }

    override fun getMaxAge(): Float {
        return preferences.getFloat(PreferencesKey.MAX_AGE, PreferencesDefault.MAX_AGE)
    }

    override fun getDistanceInMeters(): Int {
        return preferences.getFloat(PreferencesKey.DISTANCE, PreferencesDefault.DISTANCE).toInt() * 1000
    }

    override fun getDistance(): Float {
        return preferences.getFloat(PreferencesKey.DISTANCE, PreferencesDefault.DISTANCE)
    }

    override fun getAccountId(): String {
        return "e338f080-2a4b-4213-888c-5d8327720860"
    }

    override fun getEmail(): String {
        return "21@gmail.com"
    }

    override fun getLatitude(): Double {
        return preferences.getDouble(PreferencesKey.LATITUDE, PreferencesDefault.LATITUDE)
    }

    override fun getLongitude(): Double {
        return preferences.getDouble(PreferencesKey.LONGITUDE, PreferencesDefault.LONGITUDE)
    }



}