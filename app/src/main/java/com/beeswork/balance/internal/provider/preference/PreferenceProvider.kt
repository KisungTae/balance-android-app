package com.beeswork.balance.internal.provider.preference

import org.threeten.bp.OffsetDateTime
import java.util.*


interface PreferenceProvider {
    fun putSwipeFilterValues(gender: Boolean, minAge: Float, maxAge: Float, distance: Float)
    fun putAccountId(accountId: UUID?)
    fun putIdentityTokenId(identityToken: UUID?)
    fun putMatchFetchedAt(updatedAt: OffsetDateTime?)

    fun getGender(): Boolean
    fun getMinAgeBirthYear(): Int
    fun getMaxAgeBirthYear(): Int
    fun getMinAge(): Float
    fun getMaxAge(): Float
    fun getDistanceInMeters(): Int
    fun getDistance(): Float
    fun getAccountId(): UUID?
    fun getIdentityToken(): UUID?
    fun getMatchFetchedAt(): OffsetDateTime
    fun getProfilePhotoKey(): String?
}