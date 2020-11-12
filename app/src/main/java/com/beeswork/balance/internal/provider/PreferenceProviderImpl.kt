package com.beeswork.balance.internal.provider

import android.content.Context
import androidx.preference.PreferenceManager
import com.beeswork.balance.internal.*
import org.threeten.bp.OffsetDateTime
import java.util.*


const val GENDER = "gender"
const val MIN_AGE = "minAge"
const val MAX_AGE = "maxAge"
const val DISTANCE = "distance"
const val LATITUDE = "latitude"
const val LONGITUDE = "longitude"
const val MATCH_FETCHED_AT = "matchFetchedAt"
const val CLICKED_FETCHED_AT = "clickedFetchedAt"

const val DEFAULT_DISTANCE = 10f
const val DEFAULT_MIN_AGE = 20f
const val DEFAULT_MAX_AGE = 100f
const val DEFAULT_GENDER = true
const val DEFAULT_LATITUDE = 37.570475
const val DEFAULT_LONGITUDE = 126.983405


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
        editor.putBoolean(GENDER, gender)
        editor.putFloat(MIN_AGE, minAge)
        editor.putFloat(MAX_AGE, maxAge)
        editor.putFloat(DISTANCE, distance)
        editor.apply()
    }

    override fun putLocation(latitude: Double, longitude: Double) {

        editor.putDouble(LATITUDE, latitude)
        editor.putDouble(LONGITUDE, longitude)
        editor.apply()
    }

    override fun putAccountId(accountId: String) {
    }

    override fun putIdentityToken() {

    }

    override fun putMatchFetchedAt(matchFetchedAt: String) {
        editor.putString(MATCH_FETCHED_AT, matchFetchedAt)
    }

    override fun putClickedFetchedAt(clickedFetchedAt: String) {
        editor.putString(CLICKED_FETCHED_AT, clickedFetchedAt)
    }

    override fun putLocationSynced(locationSynced: Boolean) {

    }


    override fun getClickedFetchedAt(): String {
        return preferences.getString(CLICKED_FETCHED_AT, "2020-01-01T10:06:26.032Z")!!
    }


    override fun getMatchFetchedAt(): String {
        return preferences.getString(MATCH_FETCHED_AT, "2020-01-01T10:06:26.032Z")!!
    }

    override fun getGender(): Boolean {
        return preferences.getBoolean(GENDER, DEFAULT_GENDER)
    }

    override fun getMinAgeBirthYear(): Int {
        val minAge = preferences.getFloat(MIN_AGE, DEFAULT_MIN_AGE)
        return Calendar.getInstance().get(Calendar.YEAR) - minAge.toInt()
    }

    override fun getMaxAgeBirthYear(): Int {
        val maxAge = preferences.getFloat(MAX_AGE, DEFAULT_MAX_AGE)
        return Calendar.getInstance().get(Calendar.YEAR) - maxAge.toInt()
    }

    override fun getMinAge(): Float {
        return preferences.getFloat(MIN_AGE, DEFAULT_MIN_AGE)
    }

    override fun getMaxAge(): Float {
        return preferences.getFloat(MAX_AGE, DEFAULT_MAX_AGE)
    }

    override fun getDistanceInMeters(): Int {
        return preferences.getFloat(DISTANCE, DEFAULT_DISTANCE).toInt() * 1000
    }

    override fun getDistance(): Float {
        return preferences.getFloat(DISTANCE, DEFAULT_DISTANCE)
    }

    override fun getAccountId(): String {
        return "d1ca10d8-8954-42fd-958e-634fdee618d4"
    }

    override fun getIdentityToken(): String {
        return "d3c8e7cc-4ff0-4bc3-903d-185b923b64ce"
    }

    override fun getLatitude(): Double {
        return preferences.getDouble(LATITUDE, DEFAULT_LATITUDE)
    }

    override fun getLongitude(): Double {
        return preferences.getDouble(LONGITUDE, DEFAULT_LONGITUDE)
    }

    override fun isLocationSynced(): Boolean {
        return false
    }

}