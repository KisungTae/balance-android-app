package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.PhotoConstant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class FetchPhotosUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : FetchPhotosUseCase {

    override suspend fun invoke(): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                val response = photoRepository.fetchPhotos(PhotoConstant.MAX_NUM_OF_PHOTOS)
                response.data?.forEach { photo ->

                }




                return@withContext response.toEmptyResponse()
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }
}