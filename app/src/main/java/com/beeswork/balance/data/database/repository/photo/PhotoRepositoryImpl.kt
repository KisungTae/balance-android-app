package com.beeswork.balance.data.database.repository.photo

import android.net.Uri
import android.webkit.MimeTypeMap
import com.beeswork.balance.data.database.dao.PhotoDAO
import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.network.rds.photo.PhotoRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.Delimiter
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.PhotoConstant
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.PhotoNotExistException
import com.beeswork.balance.internal.exception.PhotoNotSupportedTypeException
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.util.*

class PhotoRepositoryImpl(
    private val photoRDS: PhotoRDS,
    private val photoDAO: PhotoDAO,
    private val photoMapper: PhotoMapper,
    private val preferenceProvider: PreferenceProvider,
    private val ioDispatcher: CoroutineDispatcher
) : PhotoRepository {

    override suspend fun getProfilePhoto(): Photo? {
        return withContext(ioDispatcher) {
            return@withContext photoDAO.getProfilePhotoBy(preferenceProvider.getAccountId())
        }
    }

    override fun getPhotosFlow(maxPhotoCount: Int): Flow<List<Photo>> {
        return photoDAO.getPhotoFlowBy(preferenceProvider.getAccountId(), maxPhotoCount)
    }

    override suspend fun fetchPhotos(maxNumOfPhotos: Int): Resource<List<Photo>> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
            val photos = photoDAO.getAllBy(accountId, maxNumOfPhotos)
            if (photos.isNotEmpty()) {
                return@withContext Resource.success(photos)
            }

            val response = photoRDS.fetchPhotos().map { photoDTOs ->
                photoDTOs?.map { photoDTO ->
                    photoMapper.toPhoto(photoDTO)
                }
            }

            if (response.data != null && response.data.isNotEmpty()) {
                photoDAO.insert(response.data)
            }
            return@withContext response
        }
    }

    override suspend fun uploadPhoto(
        photoFile: File,
        photoUri: Uri,
        extension: String,
        photoKey: String?
    ): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
            val photo = createPhoto(accountId, photoKey, photoUri, extension)

            val uploadPhotoToS3Response = uploadPhotoToS3(photoFile, photo, extension)
            when {
                uploadPhotoToS3Response.isExceptionCodeEqualTo(ExceptionCode.PHOTO_ALREADY_EXIST_EXCEPTION) -> {
                    photoDAO.updateStatusBy(photo.key, PhotoStatus.OCCUPIED)
                    return@withContext Resource.success(EmptyResponse())
                }
                uploadPhotoToS3Response.isError() -> {
                    photoDAO.updateStatusBy(photo.key, PhotoStatus.UPLOAD_ERROR)
                    return@withContext uploadPhotoToS3Response
                }
                uploadPhotoToS3Response.isSuccess() && !photo.uploaded -> {
                    photoDAO.updateUploadedBy(photo.key, true)
                }
            }

            val savePhotoResponse = photoRDS.savePhoto(photo.key, photo.sequence)
            if (savePhotoResponse.isSuccess()) {
                photoDAO.updateAsSavedBy(photo.key)
            } else if (savePhotoResponse.isError()) {
                photoDAO.updateStatusBy(photo.key, PhotoStatus.UPLOAD_ERROR)
            }
            return@withContext savePhotoResponse
        }
    }

    private suspend fun createPhoto(accountId: UUID, photoKey: String?, photoUri: Uri, extension: String): Photo {
        val photo = photoDAO.getBy(photoKey)?.let { photo ->
            photo.status = PhotoStatus.UPLOADING
            photo
        } ?: kotlin.run {
            val sequence = (photoDAO.getLastSequenceBy(accountId) ?: 0) + 1
            val newPhotoKey = UUID.randomUUID().toString() + Delimiter.FULL_STOP + extension
            Photo(newPhotoKey, accountId, PhotoStatus.UPLOADING, false, photoUri, sequence, sequence, false, false)
        }
        photoDAO.insert(photo)
        return photo
    }


    private suspend fun uploadPhotoToS3(photoFile: File, photo: Photo, extension: String): Resource<EmptyResponse> {
        if (photo.uploaded) {
            return Resource.success(EmptyResponse())
        }

        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: return Resource.error(
            PhotoNotSupportedTypeException()
        )

        val getPreSignedURLResponse = photoRDS.getPreSignedURL(photo.key)
        val preSignedURLDTO = getPreSignedURLResponse.data
        if (getPreSignedURLResponse.isSuccess() && preSignedURLDTO != null) {
            val formData = mutableMapOf<String, RequestBody>()
            for ((key, value) in preSignedURLDTO.fields) {
                formData[key] = RequestBody.create(MultipartBody.FORM, value)
            }
            val requestBody = RequestBody.create(MediaType.parse(mimeType), photoFile)
            val multiPartBody = MultipartBody.Part.createFormData(FILE, photo.key, requestBody)
            return photoRDS.uploadPhotoToS3(preSignedURLDTO.url, formData, multiPartBody)
        }
        return getPreSignedURLResponse.toEmptyResponse()
    }

    override suspend fun deletePhoto(photoKey: String): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val photo = photoDAO.getBy(photoKey) ?: return@withContext Resource.error(PhotoNotExistException())
            photoDAO.updateDeletingBy(photoKey, true)
            val response = photoRDS.deletePhoto(photoKey)

            if (response.isSuccess()) {
                deletePhotoFile(photo.uri)
                photoDAO.deleteBy(photo.key)
            } else {
                cancelDeletePhoto(photoKey)
            }
            return@withContext response
        }
    }

    override suspend fun cancelDeletePhoto(photoKey: String) {
        photoDAO.updateDeletingBy(photoKey, false)
    }

    private fun deletePhotoFile(photoUri: Uri?) {
        photoUri?.path?.let { path ->
            val photoFile = File(path)
            if (photoFile.exists()) {
                if (!photoFile.delete()) {
                    throw IOException()
                }
            }
        }
    }

    override suspend fun updatePhotoStatus(photoKey: String, photoStatus: PhotoStatus) {
        withContext(ioDispatcher) {
            photoDAO.updateStatusBy(photoKey, photoStatus)
        }
    }

    override suspend fun updatePhotoStatuses(photoKeys: List<String>, photoStatus: PhotoStatus) {
        withContext(ioDispatcher) {
            photoDAO.updateStatusBy(photoKeys, photoStatus)
        }
    }

    override suspend fun orderPhotos(photoSequences: Map<String, Int>): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val photos = photoDAO.getAllBy(photoSequences.keys.toList())
            if (photos.isEmpty()) {
                return@withContext Resource.success(EmptyResponse())
            }
            photos.forEach { photo ->
                val sequence = photoSequences[photo.key]
                if (sequence != null && photo.sequence != sequence) {
                    photo.status = PhotoStatus.ORDERING
                    photo.oldSequence = photo.sequence
                    photo.sequence = sequence
                }
            }
            photoDAO.insert(photos)
            val response = photoRDS.orderPhotos(photoSequences)
            photos.forEach { photo ->
                photo.status = PhotoStatus.OCCUPIED
                if (response.isError()) {
                    photo.sequence = photo.oldSequence
                }
            }
            photoDAO.insert(photos)
            response
        }
    }

    override suspend fun cancelOrderPhotos(photoKeys: List<String>) {
        val photos = photoDAO.getAllBy(photoKeys)
        if (photos.isEmpty()) {
            return
        }
        photos.forEach { photo ->
            photo.status = PhotoStatus.OCCUPIED
            photo.sequence = photo.oldSequence
        }
        photoDAO.insert(photos)
    }

    override suspend fun deletePhotos() {
        withContext(ioDispatcher) {
            val photos = photoDAO.getAllBy(preferenceProvider.getAccountId(), Int.MAX_VALUE)
            photos.forEach { photo ->
                deletePhotoFile(photo.uri)
                photoDAO.deleteBy(photo.key)
            }
        }
    }



    override suspend fun test() {
        withContext(ioDispatcher) {
//            photoDAO.insert(Photo("", PhotoStatus.EMPTY, null, 100, 100, false, false))
        }
    }

    companion object {
        const val FILE = "file"
    }
}