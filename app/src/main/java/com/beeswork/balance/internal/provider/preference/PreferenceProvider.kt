package com.beeswork.balance.internal.provider.preference

import java.util.*


interface PreferenceProvider {
    fun putValidLoginInfo(accountId: UUID?, accessToken: String?, refreshToken: String?)


    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun getAccountId(): UUID?

    fun delete()
}