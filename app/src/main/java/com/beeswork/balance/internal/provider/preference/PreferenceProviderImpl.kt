package com.beeswork.balance.internal.provider.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.IdentityTokenNotFoundException
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

    override fun putLoginInfo(accountId: UUID, identityToken: UUID, accessToken: String, refreshToken: String?) {
        editor.putString(ACCOUNT_ID, accountId.toString())
        editor.putString(IDENTITY_TOKEN, identityToken.toString())
        editor.putString(ACCESS_TOKEN, accessToken)
        refreshToken?.let { _refreshToken -> editor.putString(REFRESH_TOKEN, _refreshToken) }
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

    override fun getAccountIdOrThrow(): UUID {
        val accountIdInString = preferences.getString(ACCOUNT_ID, null)
        accountIdInString?.let { _accountIdInString ->
            val accountId = UUID.fromString(_accountIdInString)
            return accountId ?: throw AccountIdNotFoundException()
        } ?: throw AccountIdNotFoundException()
    }

    override fun getIdentityTokenOrThrow(): UUID {
        val identityTokenInString = preferences.getString(ACCOUNT_ID, null)
        identityTokenInString?.let { _identityTokenInString ->
            val identityToken = UUID.fromString(_identityTokenInString)
            return identityToken ?: throw IdentityTokenNotFoundException()
        } ?: throw IdentityTokenNotFoundException()
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