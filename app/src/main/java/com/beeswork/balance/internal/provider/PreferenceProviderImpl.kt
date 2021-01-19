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
const val MATCH_FETCHED_AT = "matchFetchedAt"
const val CLICKED_FETCHED_AT = "clickedFetchedAt"

const val DEFAULT_DISTANCE = 10f
const val DEFAULT_MIN_AGE = 20f
const val DEFAULT_MAX_AGE = 100f
const val DEFAULT_GENDER = true



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
        return "0a164463-f27f-4a68-840b-4e04c0becb3c"
//        return "9f881819-638a-4098-954c-ce34b133d32a"
    }

    override fun getIdentityToken(): String {
        return "379f3f4c-ae86-4968-908a-f9f38ef13286"
//        return "96c80a98-8807-4aee-999c-ceca92e009c3"
    }



}