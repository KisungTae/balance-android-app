package com.beeswork.balance.internal.provider.preference


interface PreferenceProvider {


    fun putSwipeFilterValues(gender: Boolean, minAge: Float, maxAge: Float, distance: Float)
    fun putAccountId(accountId: String)
    fun putIdentityToken()
    fun putMatchFetchedAt(matchFetchedAt: String)
    fun putClickedFetchedAt(clickedFetchedAt: String)

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
}