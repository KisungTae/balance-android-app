package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class OrderPhotosUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): OrderPhotosUseCase {

    override suspend fun invoke(photoSequences: Map<String, Int>): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                photoRepository.orderPhotos(photoSequences)
            }
        } catch (e: IOException) {
            photoRepository.cancelOrderPhotos(photoSequences.keys.toList())
            return Resource.error(e)
        }
    }
}