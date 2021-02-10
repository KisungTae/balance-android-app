package com.beeswork.balance.internal.provider.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.beeswork.balance.data.database.converter.OffsetDateTimeConverter
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.IdentityTokenNotFoundException
import org.threeten.bp.OffsetDateTime
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

    override fun putMatchFetchedAt(updatedAt: OffsetDateTime) {
        editor.putString(
            LAST_FETCHED_MATCH_UPDATED_AT,
            OffsetDateTimeConverter.fromOffsetDateTimeNonNull(updatedAt)
        )
    }

    override fun putAccountFetchedAt(updatedAt: OffsetDateTime) {
        editor.putString(
            LAST_FETCHED_ACCOUNT_UPDATED_AT,
            OffsetDateTimeConverter.fromOffsetDateTimeNonNull(updatedAt)
        )
    }

    override fun putChatMessageFetchedAt(createdAt: OffsetDateTime) {
        editor.putString(
            LAST_FETCHED_CHAT_MESSAGE_CREATED_AT,
            OffsetDateTimeConverter.fromOffsetDateTimeNonNull(createdAt)
        )
    }

    override fun getClickedFetchedAt(): String {
        return preferences.getString(CLICKED_FETCHED_AT, "2020-01-01T10:06:26.032Z")!!
    }


    override fun getMatchFetchedAt1(): String {
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

    override fun getAccountId1(): String {
//        return "6be75d61-b60a-44f9-916b-9703a9063cf5"
        return "01ac40b1-cc3f-4a96-9663-df0ad79acee0"
    }

    override fun getIdentityToken1(): String {
//        return "669a4e60-93f9-4f9f-8652-9328f792e3dd"
        return "e6deee15-9c06-4065-bb0d-e89e7c2f26e8"
    }

    override fun getAccountId(): UUID {
//      TODO: change getAccountId to null
        preferences.getString(ACCOUNT_ID, getAccountId1())?.let {
            return UUID.fromString(it)
        } ?: throw AccountIdNotFoundException()
    }

    override fun getIdentityToken(): UUID {
//      TODO: change getIdentityToken to null
        preferences.getString(IDENTITY_TOKEN, getIdentityToken1())?.let {
            return UUID.fromString(it)
        } ?: throw IdentityTokenNotFoundException()
    }


    override fun getMatchFetchedAt(): OffsetDateTime {
        return OffsetDateTimeConverter.toOffsetDateTimeNonNull(
            preferences.getString(
                LAST_FETCHED_MATCH_UPDATED_AT,
                DEFAULT_FETCHED_AT
            )!!
        )
    }

    override fun getAccountFetchedAt(): OffsetDateTime {
        return OffsetDateTimeConverter.toOffsetDateTimeNonNull(
            preferences.getString(
                LAST_FETCHED_ACCOUNT_UPDATED_AT,
                DEFAULT_FETCHED_AT
            )!!
        )
    }

    override fun getChatMessageFetchedAt(): OffsetDateTime {
        return OffsetDateTimeConverter.toOffsetDateTimeNonNull(
            preferences.getString(
                LAST_FETCHED_CHAT_MESSAGE_CREATED_AT,
                DEFAULT_FETCHED_AT
            )!!
        )
    }


}