package com.beeswork.balance.internal.provider.preference

import java.util.*


interface PreferenceProvider {
    fun putLoginInfo(accessToken: String, refreshToken: String)
    fun putLoginInfo(accountId: UUID, accessToken: String, refreshToken: String)

    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun getAccountId(): UUID

    fun delete()
}