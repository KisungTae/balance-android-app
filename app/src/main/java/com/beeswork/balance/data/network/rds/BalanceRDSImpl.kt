package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.CardResponse
import com.beeswork.balance.data.network.BalanceService
import com.beeswork.balance.data.network.request.ClickRequest
import com.beeswork.balance.data.network.request.FCMTokenRequest
import com.beeswork.balance.data.network.request.LocationRequest
import com.beeswork.balance.data.network.request.SwipeRequest
import com.beeswork.balance.data.network.response.BalanceGameResponse
import com.beeswork.balance.data.network.response.ClickResponse
import com.beeswork.balance.data.network.response.EmptyJsonResponse
import com.beeswork.balance.internal.Resource
import org.threeten.bp.OffsetDateTime

class BalanceRDSImpl(
    private val balanceService: BalanceService
) : BaseRDS(), BalanceRDS {


    override suspend fun fetchCards(
        accountId: String,
        identityToken: String,
        minAge: Int,
        maxAge: Int,
        gender: Boolean,
        distance: Int,
        latitude: Double?,
        longitude: Double?,
        locationUpdatedAt: String?,
        reset: Boolean
    ): Resource<MutableList<CardResponse>> {

        return getResult {
            balanceService.fetchCards(
                accountId,
                identityToken,
                minAge,
                maxAge,
                gender,
                distance,
                latitude,
                longitude,
                locationUpdatedAt,
                reset
            )
        }
    }

    override suspend fun swipe(
        accountId: String,
        identityToken: String,
        swipeId: Long?,
        swipedId: String
    ): Resource<BalanceGameResponse> {
        return getResult {
            balanceService.swipe(SwipeRequest(accountId, identityToken, swipeId, swipedId))
        }
    }

    override suspend fun click(
        accountId: String,
        identityToken: String,
        swipedId: String,
        swipeId: Long,
        answers: Map<Long, Boolean>
    ): Resource<ClickResponse> {
        return getResult {
            balanceService.click(ClickRequest(accountId, identityToken, swipedId, swipeId, answers))
        }
    }

    override suspend fun postFCMToken(
        accountId: String,
        identityToken: String,
        token: String
    ): Resource<EmptyJsonResponse> {
        return getResult {
            balanceService.postFCMToken(FCMTokenRequest(accountId, identityToken, token))
        }
    }

    override suspend fun fetchMatches(
        accountId: String,
        identityToken: String,
        fetchedAt: String
    ): Resource<MutableList<Match>> {
        return getResult {
            balanceService.fetchMatches(accountId, identityToken, fetchedAt)
        }
    }

    override suspend fun fetchClickedList(
        accountId: String,
        identityToken: String,
        fetchedAt: String
    ): Resource<MutableList<Clicked>> {
        return getResult {
            balanceService.fetchClickedList(accountId, identityToken, fetchedAt)
        }
    }

    override suspend fun postLocation(
        accountId: String,
        identityToken: String,
        latitude: Double,
        longitude: Double,
        updatedAt: String
    ): Resource<EmptyJsonResponse> {
        return getResult {
            balanceService.postLocation(LocationRequest(accountId, identityToken, latitude, longitude, updatedAt))
        }
    }

}











