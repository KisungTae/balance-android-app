package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class DeletePhotoUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : DeletePhotoUseCase {
    override suspend fun invoke(photoKey: String): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                photoRepository.deletePhoto(photoKey)
            }
        } catch (e: IOException) {
            photoRepository.cancelDeletePhoto(photoKey)
            Resource.error(e)
        } catch (e: SecurityException) {
            photoRepository.cancelDeletePhoto(photoKey)
            Resource.error(e)
        }
    }
}