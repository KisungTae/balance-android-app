package com.beeswork.balance.internal.provider.preference

import android.content.Context
import androidx.preference.PreferenceManager
import java.util.*

class PreferenceProviderImpl(
    context: Context
) : PreferenceProvider {

    private val appContext = context.applicationContext
    private val preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    private val editor = preferences.edit()

    override fun putRefreshToken(refreshToken: String) {
        editor.putString(REFRESH_TOKEN, refreshToken)
        editor.apply()
    }

    override fun putLoginInfo(accountId: UUID, accessToken: String) {
        editor.putString(ACCESS_TOKEN, accountId.toString())
        editor.putString(REFRESH_TOKEN, accessToken)
        editor.apply()
    }

    override fun putLoginInfo(accountId: UUID, accessToken: String, refreshToken: String) {
        editor.putString(ACCOUNT_ID, accountId.toString())
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
//        preferences.getString(ACCOUNT_ID, null)?.let { accountId ->
//            return Converter.toUUID(accountId)
//        } ?: return null

        return UUID.randomUUID()
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