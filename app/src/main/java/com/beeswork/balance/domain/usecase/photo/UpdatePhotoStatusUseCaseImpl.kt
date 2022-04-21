package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.internal.constant.PhotoStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdatePhotoStatusUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : UpdatePhotoStatusUseCase {
    override suspend fun invoke(photoKey: String, photoStatus: PhotoStatus) = withContext(defaultDispatcher) {
        photoRepository.updatePhotoStatus(photoKey, photoStatus)
    }
}