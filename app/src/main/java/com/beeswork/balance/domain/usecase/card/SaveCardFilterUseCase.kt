package com.beeswork.balance.domain.usecase.card

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface SaveCardFilterUseCase {

    suspend fun invoke(gender: Boolean, minAge: Int, maxAge: Int, distance: Int): Resource<EmptyResponse>
}