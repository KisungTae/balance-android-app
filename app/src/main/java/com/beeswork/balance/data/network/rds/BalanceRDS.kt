package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.BalanceGameResponse
import com.beeswork.balance.data.network.response.CardResponse
import com.beeswork.balance.data.network.response.ClickResponse
import com.beeswork.balance.data.network.response.EmptyJsonResponse
import com.beeswork.balance.internal.Resource

interface BalanceRDS {

    suspend fun fetchCards(
        accountId: String,
        email: String,
        latitude: Double,
        longitude: Double,
        minAge: Int,
        maxAge: Int,
        gender: Boolean,
        distance: Int
    ): Resource<MutableList<CardResponse>>

    suspend fun swipe(
        accountId: String,
        email: String,
        swipedId: String
    ): Resource<BalanceGameResponse>

    suspend fun click(
        accountId: String,
        email: String,
        swipedId: String,
        swipeId: Long,
        answers: Map<Long, Boolean>
    ): Resource<ClickResponse>

    suspend fun postFCMToken(
        accountId: String,
        email:String,
        token: String
    ): Resource<EmptyJsonResponse>

    suspend fun fetchMatches(
        accountId: String,
        email: String,
        fetchedAt: String
    ): Resource<MutableList<Match>>

    suspend fun fetchClickedList(
        clickedId: String,
        email: String,
        fetchedAt: String
    ): Resource<MutableList<Clicked>>
}