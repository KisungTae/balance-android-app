package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.Card
import com.beeswork.balance.data.network.BalanceService
import com.beeswork.balance.data.network.request.FCMTokenRequest
import com.beeswork.balance.data.network.request.SwipeRequest
import com.beeswork.balance.data.network.response.BalanceGame
import com.beeswork.balance.data.network.response.EmptyJsonResponse
import com.beeswork.balance.internal.Resource

class BalanceRDSImpl(
    private val balanceService: BalanceService
) : BaseRDS(), BalanceRDS {


    override suspend fun fetchCards(
        accountId: String,
        latitude: Double,
        longitude: Double,
        minAge: Int,
        maxAge: Int,
        gender: Boolean,
        distance: Int
    ): Resource<MutableList<Card>> {

        return getResult {
            balanceService.fetchCards(
                accountId,
                latitude,
                longitude,
                minAge,
                maxAge,
                gender,
                distance
            )
        }
    }

    override suspend fun swipe(
        swiperId: String,
        swiperEmail: String,
        swipedId: String
    ): Resource<BalanceGame> {
        return getResult {
            balanceService.swipe(SwipeRequest(swiperId, swiperEmail, swipedId))
        }
    }

    override suspend fun click(
        swiperId: String,
        swiperEmail: String,
        swipedId: String,
        swipeId: Long
    ): Resource<Click> {
        return getResult {
            balanceService.click(SwipeRequest(swiperId, swiperEmail, swipedId, swipeId))
        }
    }

    override suspend fun postFCMToken(
        accountId: String,
        email: String,
        token: String
    ): Resource<EmptyJsonResponse> {
        return getResult {
            balanceService.postFCMToken(FCMTokenRequest(accountId, email, token))
        }
    }

    override suspend fun fetchMatches(
        matcherId: String,
        email: String,
        fetchedAt: String
    ): Resource<MutableList<Match>> {
        return getResult {
            balanceService.fetchMatches(matcherId, email, fetchedAt)
        }
    }

    override suspend fun fetchClicked(
        clickedId: String,
        email: String,
        fetchedAt: String
    ): Resource<MutableList<Clicked>> {
        return getResult {
            balanceService.fetchClicked(clickedId, email, fetchedAt)
        }
    }

}











