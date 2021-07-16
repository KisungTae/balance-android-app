package com.beeswork.balance.internal.provider.preference

import com.beeswork.balance.internal.constant.LoginType
import org.threeten.bp.OffsetDateTime
import java.util.*


interface PreferenceProvider {
    fun putLoginType(loginType: LoginType)
    fun putJwtToken(jwtToken: String)
    fun putAccountId(accountId: UUID)
    fun putIdentityTokenId(identityToken: UUID)

    fun getLoginType(): LoginType
    fun getJwtToken(): String?
    fun getAccountId(): UUID?
    fun getIdentityToken(): UUID?

    fun delete()
}