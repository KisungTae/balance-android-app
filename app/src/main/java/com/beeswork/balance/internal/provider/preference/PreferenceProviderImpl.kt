package com.beeswork.balance.internal.provider.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.beeswork.balance.data.database.converter.OffsetDateTimeConverter
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.IdentityTokenNotFoundException
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.Exception
import java.util.*


const val GENDER = "gender"
const val MIN_AGE = "minAge"
const val MAX_AGE = "maxAge"
const val DISTANCE = "distance"
const val MATCH_FETCHED_AT = "matchFetchedAt"
const val CLICKED_FETCHED_AT = "clickedFetchedAt"
const val LAST_FETCHED_MATCH_UPDATED_AT = "lastFetchedMatchUpdatedAt"
const val LAST_FETCHED_ACCOUNT_UPDATED_AT = "lastFetchedAccountUpdatedAt"
const val LAST_FETCHED_CHAT_MESSAGE_CREATED_AT = "lastFetchedChatMessageCreatedAt"
const val ACCOUNT_ID =  "accountId"
const val IDENTITY_TOKEN =  "identityToken"


const val DEFAULT_DISTANCE = 10f
const val DEFAULT_MIN_AGE = 20f
const val DEFAULT_MAX_AGE = 100f
const val DEFAULT_GENDER = true
const val DEFAULT_FETCHED_AT = "2020-01-01T10:06:26.032Z"


class PreferenceProviderImpl(
    context: Context
) : PreferenceProvider {

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

    override fun putAccountUUID(accountId: UUID) {
        editor.putString(ACCOUNT_ID, accountId.toString())
    }

    override fun putIdentityTokenUUID(identityToken: UUID) {
        editor.putString(IDENTITY_TOKEN, identityToken.toString())
    }

    override fun putLastFetchedMatchUpdatedAt(updatedAt: OffsetDateTime) {
        editor.putString(
            LAST_FETCHED_MATCH_UPDATED_AT,
            OffsetDateTimeConverter.fromOffsetDateTimeNonNull(updatedAt)
        )
    }

    override fun putLastFetchedAccountUpdatedAt(updatedAt: OffsetDateTime) {
        editor.putString(
            LAST_FETCHED_ACCOUNT_UPDATED_AT,
            OffsetDateTimeConverter.fromOffsetDateTimeNonNull(updatedAt)
        )
    }

    override fun putLastFetchedChatMessageCreatedAt(createdAt: OffsetDateTime) {
        editor.putString(
            LAST_FETCHED_CHAT_MESSAGE_CREATED_AT,
            OffsetDateTimeConverter.fromOffsetDateTimeNonNull(createdAt)
        )
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
        return "6be75d61-b60a-44f9-916b-9703a9063cf5"
//        return "9f881819-638a-4098-954c-ce34b133d32a"
    }

    override fun getIdentityToken(): String {
        return "669a4e60-93f9-4f9f-8652-9328f792e3dd"
//        return "96c80a98-8807-4aee-999c-ceca92e009c3"
    }

    override fun getAccountUUID(): UUID {
        preferences.getString(ACCOUNT_ID, null)?.let {
            return UUID.fromString(it)
        } ?: throw AccountIdNotFoundException()
    }

    override fun getIdentityTokenUUID(): UUID {
        preferences.getString(IDENTITY_TOKEN, null)?.let {
            return UUID.fromString(it)
        } ?: throw IdentityTokenNotFoundException()
    }


    override fun getLastFetchedMatchUpdatedAt(): OffsetDateTime {
        return OffsetDateTimeConverter.toOffsetDateTimeNonNull(
            preferences.getString(
                LAST_FETCHED_MATCH_UPDATED_AT,
                DEFAULT_FETCHED_AT
            )!!
        )
    }

    override fun getLastFetchedAccountUpdatedAt(): OffsetDateTime {
        return OffsetDateTimeConverter.toOffsetDateTimeNonNull(
            preferences.getString(
                LAST_FETCHED_ACCOUNT_UPDATED_AT,
                DEFAULT_FETCHED_AT
            )!!
        )
    }

    override fun getLastFetchedChatMessageCreatedAt(): OffsetDateTime {
        return OffsetDateTimeConverter.toOffsetDateTimeNonNull(
            preferences.getString(
                LAST_FETCHED_CHAT_MESSAGE_CREATED_AT,
                DEFAULT_FETCHED_AT
            )!!
        )
    }


}