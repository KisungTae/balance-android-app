package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.PhotoConstant
import com.beeswork.balance.internal.constant.PhotoStatus
import kotlinx.coroutines.*
import java.io.IOException

class SyncPhotosUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val orderPhotosUseCase: OrderPhotosUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SyncPhotosUseCase {
    override suspend fun invoke(): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                val response = photoRepository.fetchPhotos(PhotoConstant.MAX_NUM_OF_PHOTOS)
                val photoSequences = mutableMapOf<String, Int>()
                val photoKeysToDownload = mutableListOf<String>()

                response.data?.forEach { photo ->
                    when {
                        photo.deleting -> launch {
                            deletePhotoUseCase.invoke(photo.key)
                        }
                        photo.status == PhotoStatus.OCCUPIED || photo.status == PhotoStatus.DOWNLOAD_ERROR -> {
                            photoKeysToDownload.add(photo.key)
                        }
                        photo.status == PhotoStatus.UPLOAD_ERROR || photo.status == PhotoStatus.UPLOADING -> launch {
                            uploadPhotoUseCase.invoke(photo.uri, photo.key)
                        }
                        photo.status == PhotoStatus.ORDERING -> {
                            photoSequences[photo.key] = photo.sequence
                        }
                    }
                }
                if (photoKeysToDownload.isNotEmpty()) launch {
                    photoRepository.updatePhotoStatuses(photoKeysToDownload, PhotoStatus.DOWNLOADING)
                }

                if (photoSequences.isNotEmpty()) launch {
                    orderPhotosUseCase.invoke(photoSequences)
                }
                return@withContext response.toEmptyResponse()
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }
}