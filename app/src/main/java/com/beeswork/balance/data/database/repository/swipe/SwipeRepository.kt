package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.entity.SwipeFilter
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.data.network.response.swipe.FetchCardsDTO
import com.beeswork.balance.internal.constant.Gender
import java.util.*

interface SwipeRepository {
    suspend fun deleteSwipes()
    suspend fun getSwipeFilter(): SwipeFilter
    suspend fun saveSwipeFilter(gender: Boolean, minAge: Int, maxAge: Int, distance: Int)
    suspend fun fetchCards(): Resource<FetchCardsDTO>
    suspend fun swipe(swipedId: UUID): Resource<List<QuestionDTO>>
    suspend fun prepopulateSwipeFilter()
}