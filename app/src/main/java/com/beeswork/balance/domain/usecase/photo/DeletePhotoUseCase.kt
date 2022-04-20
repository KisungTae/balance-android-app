package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface DeletePhotoUseCase {

    suspend fun invoke(photoKey: String): Resource<EmptyResponse>
}