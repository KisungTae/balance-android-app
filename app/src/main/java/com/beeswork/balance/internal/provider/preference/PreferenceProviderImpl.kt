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

    override fun getAccountId(): UUID? {
//      TODO: remove accountId and put null for default value

//        val accountId = "136d4f5e-469c-4fc0-9d7d-d04c895bf99d"
//        val accountId = "2bfe36f7-2be7-4001-aa6c-6e812f0f2d26"

        val accountId = "039ddaa0-b861-457b-ab47-e4e3978ccc2f"
//        val accountId = "6a391ce3-d13d-4ec1-9bbe-f522a8a76e31"
        return preferences.getString(ACCOUNT_ID, accountId)?.let { UUID.fromString(it) }
    }

    override fun getIdentityToken(): UUID? {
//      TODO: remove identityToken and put null for default value

//        val identityToken = "c5f74ffd-1254-4323-b13f-3ff1dfa42674"
//        val identityToken = "4a7ce75f-bb6a-4798-8781-099bc03d450e"


        val identityToken = "39ef7176-3d2c-47f7-a651-f1d26c4bbfcf"
//        val identityToken = "47cb 24fb-744e-4d6b-8f53-336d3ef43157"
        return preferences.getString(IDENTITY_TOKEN, identityToken)?.let { UUID.fromString(it) }
    }

    override fun getMatchFetchedAt(): OffsetDateTime {
        preferences.getString(MATCH_FETCHED_AT, DEFAULT_FETCHED_AT)?.let {
            return OffsetDateTimeConverter.toOffsetDateTimeNonNull(it)
        } ?: kotlin.run {
            return OffsetDateTimeConverter.toOffsetDateTimeNonNull(DEFAULT_FETCHED_AT)
        }
    }

}