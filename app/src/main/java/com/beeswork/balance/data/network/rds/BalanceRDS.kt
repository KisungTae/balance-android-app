package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.BalanceGame
import com.beeswork.balance.data.network.response.Card
import com.beeswork.balance.data.network.response.EmptyJsonResponse
import com.beeswork.balance.internal.Resource
import retrofit2.http.Query

interface BalanceRDS {

    suspend fun fetchCards(
        accountId: String,
        latitude: Double,
        longitude: Double,
        minAge: Int,
        maxAge: Int,
        gender: Boolean,
        distance: Int
    ): Resource<MutableList<Card>>

    suspend fun swipe(
        swiperId: String,
        swiperEmail: String,
        swipedId: String
    ): Resource<BalanceGame>

    suspend fun click(
        swiperId: String,
        swiperEmail: String,
        swipedId: String,
        swipeId: Long
    ): Resource<Click>

    suspend fun postFCMToken(
        accountId: String,
        email:String,
        token: String
    ): Resource<EmptyJsonResponse>

    suspend fun fetchMatches(
        matcherId: String,
        email: String,
        fetchedAt: String
    ): Resource<MutableList<Match>>

    suspend fun fetchClicked(
        clickedId: String,
        email: String,
        fetchedAt: String
    ): Resource<MutableList<Clicked>>
}