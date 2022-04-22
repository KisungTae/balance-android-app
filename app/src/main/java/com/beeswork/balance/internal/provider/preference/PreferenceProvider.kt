package com.beeswork.balance.internal.provider.preference

import java.util.*


interface PreferenceProvider {
    fun putLoginInfo(accountId: UUID, accessToken: String, refreshToken: String?, balancePhotoBucketURL: String)
    fun putAccessInfo(accessToken: String, refreshToken: String?, balancePhotoBucketURL: String)
    fun putAccessInfo(accessToken: String, refreshToken: String?)



    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun getAccountId(): UUID?
    fun getAppToken(): UUID
    fun getBalancePhotoBucketURL(): String?

    fun delete()
}