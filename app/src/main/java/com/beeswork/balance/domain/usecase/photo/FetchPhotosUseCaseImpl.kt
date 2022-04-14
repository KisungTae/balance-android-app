package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

class FetchPhotosUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : FetchPhotosUseCase {

    override suspend fun invoke(): Resource<EmptyResponse> = withContext(defaultDispatcher) {
        try {
            delay(3000)
//            return@withContext photoRepository.fetchPhotos()
            return@withContext Resource.error(IOException())
        } catch (e: IOException) {
            return@withContext Resource.error(e)
        }
    }
}