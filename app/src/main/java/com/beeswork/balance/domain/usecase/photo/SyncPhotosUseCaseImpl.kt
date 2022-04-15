package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.internal.constant.PhotoConstant
import com.beeswork.balance.internal.constant.PhotoStatus
import kotlinx.coroutines.*

class SyncPhotosUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SyncPhotosUseCase {
    override suspend fun invoke() = withContext(defaultDispatcher) {
        val photos = photoRepository.listPhotos(PhotoConstant.MAX_NUM_OF_PHOTOS)
        val photosToOrder = mutableListOf<Photo>()
        photos.forEach { photo ->

            when (photo.status) {




                PhotoStatus.ORDERING -> {
                    photosToOrder.add(photo)
                }
            }
        }
    }
}