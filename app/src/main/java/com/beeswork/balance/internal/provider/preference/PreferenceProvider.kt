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
    fun putLastFetchedMatchUpdatedAt(updatedAt: OffsetDateTime)
    fun putLastFetchedAccountUpdatedAt(updatedAt: OffsetDateTime)
    fun putLastFetchedChatMessageCreatedAt(createdAt: OffsetDateTime)

    fun getClickedFetchedAt(): String
    fun getMatchFetchedAt(): String
    fun getGender(): Boolean
    fun getMinAgeBirthYear(): Int
    fun getMaxAgeBirthYear(): Int
    fun getMinAge(): Float
    fun getMaxAge(): Float
    fun getDistanceInMeters(): Int
    fun getDistance(): Float
    fun getAccountId(): String
    fun getIdentityToken(): String
    fun getAccountUUID(): UUID
    fun getIdentityTokenUUID(): UUID
    fun getLastFetchedMatchUpdatedAt(): OffsetDateTime
    fun getLastFetchedAccountUpdatedAt(): OffsetDateTime
    fun getLastFetchedChatMessageCreatedAt(): OffsetDateTime
}