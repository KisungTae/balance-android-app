package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface SaveProfileUseCase {

    suspend fun invoke(): Resource<EmptyResponse>
}