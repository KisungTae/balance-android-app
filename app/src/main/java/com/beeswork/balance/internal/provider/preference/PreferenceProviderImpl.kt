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



    override fun getAccountId(): UUID? {
//      TODO: remove accountId and put null for default value

        val accountId = "ec16330d-908f-4987-9e3f-a58b4ceebffd"
//        val accountId = "698f2eb6-3fef-4ee3-9c7d-3e527740548e"

//        val accountId = "c2e68bd9-586b-487a-8d90-a6690516cdcd"
//        val accountId = "5b4525ba-b325-4752-ae0e-00ece9201d3b"
        return preferences.getString(ACCOUNT_ID, accountId)?.let { UUID.fromString(it) }
    }

    override fun getIdentityToken(): UUID? {
//      TODO: remove identityToken and put null for default value

        val identityToken = "cd111941-054e-43ac-880e-8074b3546207"
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

    companion object {

        const val MATCH_FETCHED_AT = "matchFetchedAt"
        const val CLICK_FETCHED_AT = "clickFetchedAt"
        const val ACCOUNT_ID = "accountId"
        const val IDENTITY_TOKEN = "identityToken"
        const val PROFILE_PHOTO_KEY = "profilePhotoKey"
        const val NAME = "name"

        const val DEFAULT_FETCHED_AT = "2020-01-01T10:06:26.032+11:00"

    }
}