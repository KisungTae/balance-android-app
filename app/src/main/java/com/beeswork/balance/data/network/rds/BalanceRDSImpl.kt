package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.entity.Click
import com.beeswork.balance.data.network.response.Card
import com.beeswork.balance.data.network.BalanceService
import com.beeswork.balance.data.network.request.Swipe
import com.beeswork.balance.data.network.response.BalanceGame
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
            balanceService.fetchCardsAsync(accountId,
                                           latitude,
                                           longitude,
                                           minAge,
                                           maxAge,
                                           gender,
                                           distance)
        }
    }

    override suspend fun swipe(
        swiperId: String,
        swiperEmail: String,
        swipedId: String
    ): Resource<BalanceGame> {
        return getResult {
            balanceService.swipe(Swipe(swiperId, swiperEmail, swipedId))
        }
    }

    override suspend fun click(
        swiperId: String,
        swiperEmail: String,
        swipedId: String,
        swipeId: Long
    ): Resource<Click> {
        return getResult {
            balanceService.click(Swipe(swiperId, swiperEmail, swipedId, swipeId))
        }
    }

}











