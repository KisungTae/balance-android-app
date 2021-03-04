package com.beeswork.balance.internal.provider.preference

import org.threeten.bp.OffsetDateTime
import java.util.*


interface PreferenceProvider {
    fun putSwipeFilterValues(gender: Boolean, minAge: Float, maxAge: Float, distance: Float)
    fun putAccountId(accountId: String)
    fun putIdentityToken()
    fun putMatchFetchedAt(matchFetchedAt: String)
    fun putClickedFetchedAt(clickedFetchedAt: String)
    fun putAccountUUID(accountId: UUID)
    fun putIdentityTokenUUID(identityToken: UUID)
    fun putMatchFetchedAt(updatedAt: OffsetDateTime)

    fun getClickedFetchedAt(): String
    fun getMatchFetchedAt1(): String
    fun getGender(): Boolean
    fun getMinAgeBirthYear(): Int
    fun getMaxAgeBirthYear(): Int
    fun getMinAge(): Float
    fun getMaxAge(): Float
    fun getDistanceInMeters(): Int
    fun getDistance(): Float
    fun getAccountId1(): String
    fun getIdentityToken1(): String
    fun getAccountId(): UUID
    fun getIdentityToken(): UUID
    fun getMatchFetchedAt(): OffsetDateTime
}