package com.beeswork.balance.domain.usecase.photo

import android.net.Uri
import android.webkit.MimeTypeMap
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.PhotoConstant
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.internal.exception.PhotoNotExistException
import com.beeswork.balance.internal.exception.PhotoNotSupportedTypeException
import com.beeswork.balance.internal.exception.PhotoOverSizeException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class UploadPhotoUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : UploadPhotoUseCase {

    override suspend fun invoke(photoUri: Uri?, photoKey: String?): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                val photoUriPath = photoUri?.path ?: return@withContext getErrorResource(PhotoNotExistException(), photoKey)
                val photoFile = File(photoUriPath)
                if (!photoFile.exists()) {
                    return@withContext getErrorResource(PhotoNotExistException(), photoKey)
                } else if (photoFile.length() > PhotoConstant.MAX_NUM_OF_PHOTOS) {
                    return@withContext getErrorResource(PhotoOverSizeException(), photoKey)
                }
                val extension = MimeTypeMap.getFileExtensionFromUrl(photoUriPath)
                if (!PhotoConstant.PHOTO_EXTENSIONS.contains(extension)) {
                    return@withContext getErrorResource(PhotoNotSupportedTypeException(), photoKey)
                }
                return@withContext photoRepository.uploadPhoto(photoFile, photoUri, extension, photoKey)
            }
        } catch (e: IOException) {
            return getErrorResource(e, photoKey)
        }
    }


    private suspend fun getErrorResource(exception: Throwable, photoKey: String?): Resource<EmptyResponse> {
        if (photoKey != null) {
            photoRepository.updatePhotoStatus(photoKey, PhotoStatus.UPLOAD_ERROR)
        }
        return Resource.error(exception)
    }
}