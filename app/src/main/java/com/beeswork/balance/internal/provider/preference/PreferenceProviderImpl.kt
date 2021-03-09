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
const val ACCOUNT_ID = "accountId"
const val IDENTITY_TOKEN = "identityToken"


const val DEFAULT_DISTANCE = 10f
const val DEFAULT_MIN_AGE = 20f
const val DEFAULT_MAX_AGE = 100f
const val DEFAULT_GENDER = true
const val DEFAULT_FETCHED_AT = "2020-01-01T10:06:26.032+11:00"


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
            editor.putString(
                MATCH_FETCHED_AT,
                OffsetDateTimeConverter.fromOffsetDateTimeNonNull(updatedAt)
            )
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

    override fun getAccountId(): UUID {
//      TODO: remove accountId and put null for default value

//        val accountId = "69161188-1ba5-484c-860b-00faf97fa962"
        val accountId = "93ad368a-80ce-4f9e-922d-4e7c230edd5a"
        preferences.getString(ACCOUNT_ID, accountId)?.let {
            return UUID.fromString(it)
        } ?: throw AccountIdNotFoundException()
    }

    override fun getIdentityToken(): UUID {
//      TODO: remove identityToken and put null for default value

//        val identityToken = "bb3c422f-3db3-4a40-a690-458a71ddb98b"
        val identityToken = "50263aa1-1b0f-4d2f-a5d6-24b9043ead59"
        preferences.getString(IDENTITY_TOKEN, identityToken)?.let {
            return UUID.fromString(it)
        } ?: throw IdentityTokenNotFoundException()
    }

    override fun getMatchFetchedAt(): OffsetDateTime {
        preferences.getString(MATCH_FETCHED_AT, DEFAULT_FETCHED_AT)?.let {
            return OffsetDateTimeConverter.toOffsetDateTimeNonNull(it)
        } ?: kotlin.run {
            return OffsetDateTimeConverter.toOffsetDateTimeNonNull(DEFAULT_FETCHED_AT)
        }
    }

}