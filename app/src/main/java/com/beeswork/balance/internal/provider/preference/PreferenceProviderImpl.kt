package com.beeswork.balance.internal.provider.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.beeswork.balance.internal.util.Converter
import java.util.*

class PreferenceProviderImpl(
    context: Context
) : PreferenceProvider {

    private val appContext = context.applicationContext
    private val preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    private val editor = preferences.edit()

    override fun putValidLoginInfo(accountId: UUID?, accessToken: String?, refreshToken: String?) {
        if (accountId != null) editor.putString(ACCOUNT_ID, accountId.toString())
        if (!accessToken.isNullOrBlank()) editor.putString(ACCESS_TOKEN, accessToken)
        if (!refreshToken.isNullOrBlank()) editor.putString(REFRESH_TOKEN, refreshToken)
        editor.apply()
    }

    override fun getAccessToken(): String? {
        return preferences.getString(ACCESS_TOKEN, null)
    }

    override fun getRefreshToken(): String? {
        return preferences.getString(REFRESH_TOKEN, null)
    }

    override fun getAccountId(): UUID? {
        return Converter.toUUID(preferences.getString(ACCOUNT_ID, null))
    }

    override fun delete() {
        editor.clear().commit()
    }

    companion object {
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val ACCOUNT_ID = "accountId"
    }
}