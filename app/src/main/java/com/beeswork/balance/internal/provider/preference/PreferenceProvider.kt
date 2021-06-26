package com.beeswork.balance.internal.provider.preference

import org.threeten.bp.OffsetDateTime
import java.util.*


interface PreferenceProvider {
    fun putAccountId(accountId: UUID?)
    fun putIdentityTokenId(identityToken: UUID?)
    fun putName(name: String)
    fun putMatchFetchedAt(updatedAt: OffsetDateTime?)
    fun putClickFetchedAt(updatedAt: OffsetDateTime?)

    fun getAccountId(): UUID?
    fun getIdentityToken(): UUID?
    fun getName(): String
    fun getMatchFetchedAt(): OffsetDateTime
    fun getClickFetchedAt(): OffsetDateTime
    fun getProfilePhotoKey(): String?

    fun delete()
}