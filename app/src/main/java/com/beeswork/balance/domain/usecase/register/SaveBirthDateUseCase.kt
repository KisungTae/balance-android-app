package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface SaveBirthDateUseCase {

    suspend fun invoke(year: Int, month: Int, day: Int): Resource<EmptyResponse>
}