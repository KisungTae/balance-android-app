package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface SaveAboutUseCase {

    suspend fun invoke(about: String): Resource<EmptyResponse>
}