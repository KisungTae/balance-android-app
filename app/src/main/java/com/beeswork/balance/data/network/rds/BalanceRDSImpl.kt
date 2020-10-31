package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.CardResponse
import com.beeswork.balance.data.network.BalanceService
import com.beeswork.balance.data.network.request.ClickRequest
import com.beeswork.balance.data.network.request.FCMTokenRequest
import com.beeswork.balance.data.network.request.SwipeRequest
import com.beeswork.balance.data.network.response.BalanceGameResponse
import com.beeswork.balance.data.network.response.ClickResponse
import com.beeswork.balance.data.network.response.EmptyJsonResponse
import com.beeswork.balance.internal.Resource

class BalanceRDSImpl(
    private val balanceService: BalanceService
) : BaseRDS(), BalanceRDS {


    override suspend fun fetchCards(
        accountId: String,
        email: String,
        latitude: Double,
        longitude: Double,
        minAge: Int,
        maxAge: Int,
        gender: Boolean,
        distance: Int
    ): Resource<MutableList<CardResponse>> {

        return getResult {
            balanceService.fetchCards(
                accountId,
                email,
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
        accountId: String,
        email: String,
        swipedId: String
    ): Resource<BalanceGameResponse> {
        return getResult {
            balanceService.swipe(SwipeRequest(accountId, email, swipedId))
        }
    }

    override suspend fun click(
        accountId: String,
        email: String,
        swipedId: String,
        swipeId: Long,
        answers: Map<Long, Boolean>
    ): Resource<ClickResponse> {
        return getResult {
            balanceService.click(ClickRequest(accountId, email, swipedId, swipeId, answers))
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
        accountId: String,
        email: String,
        fetchedAt: String
    ): Resource<MutableList<Match>> {
        return getResult {
            balanceService.fetchMatches(accountId, email, fetchedAt)
        }
    }

    override suspend fun fetchClickedList(
        clickedId: String,
        email: String,
        fetchedAt: String
    ): Resource<MutableList<Clicked>> {
        return getResult {
            balanceService.fetchClickedList(clickedId, email, fetchedAt)
        }
    }

}











