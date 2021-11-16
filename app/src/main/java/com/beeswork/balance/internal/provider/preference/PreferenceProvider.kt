package com.beeswork.balance.internal.provider.preference

import java.util.*


interface PreferenceProvider {
    fun putAccessToken(accessToken: String)
    fun putRefreshToken(refreshToken: String)
    fun putAccountId(accountId: UUID)
    fun putIdentityTokenId(identityToken: UUID)
    fun putLoginInfo(accountId: UUID, identityToken: UUID, accessToken: String, refreshToken: String?)


    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun getAccountId(): UUID
    fun getIdentityToken(): UUID
    fun getAccountIdOrThrow(): UUID
    fun getIdentityTokenOrThrow(): UUID

    fun delete()
}