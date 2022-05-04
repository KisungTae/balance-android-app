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

    override fun putLoginInfo(accountId: UUID, accessToken: String, refreshToken: String?, photoDomain: String) {
        editor.putString(ACCOUNT_ID, accountId.toString())
        editor.putString(ACCESS_TOKEN, accessToken)
        if (refreshToken != null) {
            editor.putString(REFRESH_TOKEN, refreshToken)
        }
        editor.putString(PHOTO_DOMAIN, photoDomain)
        editor.apply()
    }

    override fun putAccessInfo(accessToken: String, refreshToken: String?, photoDomain: String) {
        editor.putString(ACCESS_TOKEN, accessToken)
        if (refreshToken != null) {
            editor.putString(REFRESH_TOKEN, refreshToken)
        }
        editor.putString(PHOTO_DOMAIN, photoDomain)
        editor.apply()
    }

    override fun putAccessInfo(accessToken: String, refreshToken: String?) {
        editor.putString(ACCESS_TOKEN, accessToken)
        if (refreshToken != null) {
            editor.putString(REFRESH_TOKEN, refreshToken)
        }
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

    override fun getAppToken(): UUID {
        val appToken = Converter.toUUID(preferences.getString(APP_TOKEN, null))
        if (appToken == null) {
            val newAppToken = UUID.randomUUID()
            editor.putString(APP_TOKEN, newAppToken.toString())
            editor.apply()
            return newAppToken
        }
        return appToken
    }

    override fun getPhotoDomain(): String? {
        return preferences.getString(PHOTO_DOMAIN, null)
    }

    override fun delete() {
        editor.clear().commit()
    }

    companion object {
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val ACCOUNT_ID = "accountId"
        const val APP_TOKEN = "appToken"
        const val PHOTO_DOMAIN = "photoDomain"
    }
}