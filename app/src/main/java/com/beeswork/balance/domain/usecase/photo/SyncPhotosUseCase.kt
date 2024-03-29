package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface SyncPhotosUseCase {

    suspend fun invoke(): Resource<EmptyResponse>
}