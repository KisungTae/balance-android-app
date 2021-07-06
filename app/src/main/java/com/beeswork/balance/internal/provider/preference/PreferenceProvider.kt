package com.beeswork.balance.internal.provider.preference

import com.beeswork.balance.internal.constant.LoginType
import org.threeten.bp.OffsetDateTime
import java.util.*


interface PreferenceProvider {
    fun putLoginType(loginType: LoginType)
    fun putJwtToken(jwtToken: String)
    fun putAccountId(accountId: UUID)
    fun putIdentityTokenId(identityToken: UUID)
    fun putName(name: String)
    fun putMatchFetchedAt(updatedAt: OffsetDateTime?)
    fun putClickFetchedAt(updatedAt: OffsetDateTime?)

    fun getLoginType(): LoginType?
    fun getJwtToken(): String?
    fun getAccountId(): UUID?
    fun getIdentityToken(): UUID?
    fun getName(): String
    fun getMatchFetchedAt(): OffsetDateTime
    fun getClickFetchedAt(): OffsetDateTime
    fun getProfilePhotoKey(): String?

    fun delete()
}