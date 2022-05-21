package com.beeswork.balance.domain.usecase.profile

import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface SaveBioUseCase {

    suspend fun invoke(height: Int?, about: String?): Resource<EmptyResponse>
}