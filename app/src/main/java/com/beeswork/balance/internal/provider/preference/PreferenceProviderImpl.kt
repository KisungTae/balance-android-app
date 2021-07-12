package com.beeswork.balance.internal.provider.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.beeswork.balance.data.database.converter.OffsetDateTimeConverter
import com.beeswork.balance.internal.constant.LoginType
import org.threeten.bp.OffsetDateTime
import java.util.*

class PreferenceProviderImpl(
    context: Context
) : PreferenceProvider {

    private val appContext = context.applicationContext
    private val preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    private val editor = preferences.edit()

    override fun putLoginType(loginType: LoginType) {
        editor.putInt(LOGIN_TYPE, loginType.ordinal)
        editor.apply()
    }

    override fun putJwtToken(jwtToken: String) {
        editor.putString(ACCESS_TOKEN, jwtToken)
        editor.apply()
    }


    override fun putAccountId(accountId: UUID) {
        editor.putString(ACCOUNT_ID, accountId.toString())
        editor.apply()
    }

    override fun putIdentityTokenId(identityToken: UUID) {
        editor.putString(ACCOUNT_ID, identityToken.toString())
        editor.apply()
    }

    override fun putName(name: String) {
        editor.putString(NAME, name)
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

    override fun getLoginType(): LoginType? {
        val loginTypeOrdinal = preferences.getInt(LOGIN_TYPE, -1)
        return if (loginTypeOrdinal == -1) null
        else LoginType.values()[loginTypeOrdinal]
    }

    override fun getJwtToken(): String? {
        return preferences.getString(ACCESS_TOKEN, null)
    }


    override fun getAccountId(): UUID? {
//      TODO: remove accountId and put null for default value

        val accountId = "b40cc821-b81e-4eab-b510-118a24ae3297"
//        val accountId = "698f2eb6-3fef-4ee3-9c7d-3e527740548e"

//        val accountId = "c2e68bd9-586b-487a-8d90-a6690516cdcd"
//        val accountId = "5b4525ba-b325-4752-ae0e-00ece9201d3b"
        return preferences.getString(ACCOUNT_ID, accountId)?.let { UUID.fromString(it) }
    }

    override fun getIdentityToken(): UUID? {
//      TODO: remove identityToken and put null for default value

        val identityToken = "7fb5e45e-c8b4-477f-bf0b-fafc58bd70d7"
//        val identityToken = "f4e06ba3-1e41-47c1-8999-f281c9a2e7b7"


//        val identityToken = "83fb1c9c-a7a4-4cd3-90a5-8a2ff461db1d"
//        val identityToken = "925e289f-35fc-4ad5-93c8-82b541df2c82"
        return preferences.getString(IDENTITY_TOKEN, identityToken)?.let { UUID.fromString(it) }
    }

    override fun getName(): String {
//      TODO: remove Michael
        return preferences.getString(NAME, "Michael") ?: ""
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

    override fun delete() {
        editor.clear().commit()
    }


    companion object {
        const val LOGIN_TYPE = "loginType"
        const val ACCESS_TOKEN = "accessToken"
        const val MATCH_FETCHED_AT = "matchFetchedAt"
        const val CLICK_FETCHED_AT = "clickFetchedAt"
        const val ACCOUNT_ID = "accountId"
        const val IDENTITY_TOKEN = "identityToken"
        const val PROFILE_PHOTO_KEY = "profilePhotoKey"
        const val NAME = "name"

        const val DEFAULT_FETCHED_AT = "2020-01-01T10:06:26.032+11:00"

    }
}