package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface OrderPhotosUseCase {

    suspend fun invoke(photoSequences: Map<String, Int>): Resource<EmptyResponse>
}