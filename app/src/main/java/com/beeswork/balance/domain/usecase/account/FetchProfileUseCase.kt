package com.beeswork.balance.domain.usecase.account

import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface FetchProfileUseCase {

    suspend fun invoke(sync: Boolean): Resource<Profile>
}