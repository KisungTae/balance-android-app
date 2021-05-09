package com.beeswork.balance.internal.provider.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.beeswork.balance.data.database.converter.OffsetDateTimeConverter
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.IdentityTokenNotFoundException
import org.threeten.bp.OffsetDateTime
import java.util.*





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

    override fun putAccountId(accountId: UUID?) {
        accountId?.let {
            editor.putString(ACCOUNT_ID, accountId.toString())
        }
        editor.apply()
    }

    override fun putIdentityTokenId(identityToken: UUID?) {
        identityToken?.let {
            editor.putString(ACCOUNT_ID, identityToken.toString())
        }
        editor.apply()
    }

    override fun putMatchFetchedAt(updatedAt: OffsetDateTime?) {
        updatedAt?.let {
            editor.putString(MATCH_FETCHED_AT, OffsetDateTimeConverter.fromOffsetDateTimeNonNull(updatedAt))
        }
        editor.apply()
    }

    override fun putClickFetchedAt(updatedAt: OffsetDateTime?) {
        updatedAt?.let {
            editor.putString(CLICK_FETCHED_AT, OffsetDateTimeConverter.fromOffsetDateTimeNonNull(updatedAt))
        }
        editor.apply()
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

    override fun getAccountId(): UUID? {
//      TODO: remove accountId and put null for default value

        val accountId = "fbd1b88f-1499-41f0-8d20-0c31a7d73860"
//        val accountId = "698f2eb6-3fef-4ee3-9c7d-3e527740548e"

//        val accountId = "1b621dfe-63a5-4f8e-8d84-eb9bc72a47c5"
//        val accountId = "5b4525ba-b325-4752-ae0e-00ece9201d3b"
        return preferences.getString(ACCOUNT_ID, accountId)?.let { UUID.fromString(it) }
    }

    override fun getIdentityToken(): UUID? {
//      TODO: remove identityToken and put null for default value

        val identityToken = "1b621dfe-63a5-4f8e-8d84-eb9bc72a47c5"
//        val identityToken = "f4e06ba3-1e41-47c1-8999-f281c9a2e7b7"


//        val identityToken = "39ef7176-3d2c-47f7-a651-f1d26c4bbfcf"
//        val identityToken = "925e289f-35fc-4ad5-93c8-82b541df2c82"
        return preferences.getString(IDENTITY_TOKEN, identityToken)?.let { UUID.fromString(it) }
    }

    override fun getMatchFetchedAt(): OffsetDateTime {
        preferences.getString(MATCH_FETCHED_AT, DEFAULT_FETCHED_AT)?.let {
            return OffsetDateTimeConverter.toOffsetDateTimeNonNull(it)
        } ?: kotlin.run {
            return OffsetDateTimeConverter.toOffsetDateTimeNonNull(DEFAULT_FETCHED_AT)
        }
    }

    override fun getClickFetchedAt(): OffsetDateTime {
        preferences.getString(CLICK_FETCHED_AT, DEFAULT_FETCHED_AT)?.let {
            return OffsetDateTimeConverter.toOffsetDateTimeNonNull(it)
        } ?: kotlin.run {
            return OffsetDateTimeConverter.toOffsetDateTimeNonNull(DEFAULT_FETCHED_AT)
        }
    }

    override fun getProfilePhotoKey(): String? {
        return preferences.getString(PROFILE_PHOTO_KEY, null)
    }

    companion object {
        const val GENDER = "gender"
        const val MIN_AGE = "minAge"
        const val MAX_AGE = "maxAge"
        const val DISTANCE = "distance"
        const val MATCH_FETCHED_AT = "matchFetchedAt"
        const val CLICK_FETCHED_AT = "clickFetchedAt"
        const val ACCOUNT_ID = "accountId"
        const val IDENTITY_TOKEN = "identityToken"
        const val PROFILE_PHOTO_KEY = "profilePhotoKey"

        const val DEFAULT_DISTANCE = 10f
        const val DEFAULT_MIN_AGE = 20f
        const val DEFAULT_MAX_AGE = 100f
        const val DEFAULT_GENDER = true
        const val DEFAULT_FETCHED_AT = "2020-01-01T10:06:26.032+11:00"

    }
}