package com.beeswork.balance.internal.provider.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.beeswork.balance.internal.constant.LoginType
import java.util.*

class PreferenceProviderImpl(
    context: Context
) : PreferenceProvider {

    private val appContext = context.applicationContext
    private val preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    private val editor = preferences.edit()

    override fun putAccessToken(accessToken: String) {
        editor.putString(ACCESS_TOKEN, accessToken)
        editor.apply()
    }

    override fun putRefreshToken(refreshToken: String) {
        editor.putString(REFRESH_TOKEN, refreshToken)
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

    override fun putTokens(accountId: UUID, identityToken: UUID, accessToken: String, refreshToken: String) {
        editor.putString(ACCOUNT_ID, accountId.toString())
        editor.putString(ACCOUNT_ID, identityToken.toString())
        editor.putString(ACCESS_TOKEN, accessToken)
        editor.putString(REFRESH_TOKEN, refreshToken)
        editor.apply()
    }


    override fun getAccessToken(): String? {
        return preferences.getString(ACCESS_TOKEN, null)
    }

    override fun getRefreshToken(): String? {
        return preferences.getString(REFRESH_TOKEN, null)
    }


    override fun getAccountId(): UUID {
        return UUID.fromString(preferences.getString(ACCOUNT_ID, null)!!)
    }

    override fun getIdentityToken(): UUID {
        return UUID.fromString(preferences.getString(IDENTITY_TOKEN, null)!!)
    }

    override fun delete() {
        editor.clear().commit()
    }


    companion object {
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val ACCOUNT_ID = "accountId"
        const val IDENTITY_TOKEN = "identityToken"
    }
}