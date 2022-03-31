package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface SaveHeightUseCase {

    suspend fun invoke(height: Int): Resource<EmptyResponse>
}