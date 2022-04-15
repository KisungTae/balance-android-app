package com.beeswork.balance.data.database.repository.photo

import android.net.Uri
import android.webkit.MimeTypeMap
import com.beeswork.balance.data.database.dao.PhotoDAO
import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.network.rds.photo.PhotoRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
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

    //  NOTE 1. because it only fetches when no photo is in database, it's okay to insert them all without checking duplicates
    override suspend fun fetchPhotos(): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())

            if (photoDAO.getCountBy(accountId) <= 0) {
                val response = photoRDS.fetchPhotos()
                response.data?.let { photoDTOs ->
                    val photos = photoDTOs.map { photoDTO ->
                        photoMapper.toPhoto(photoDTO)
                    }
                    photoDAO.insert(photos)
                }
                return@withContext response.toEmptyResponse()
            }
            return@withContext Resource.success(EmptyResponse())
        }
    }

    override fun getPhotosFlow(maxPhotoCount: Int): Flow<List<Photo>> {
        return photoDAO.getPhotoFlowBy(preferenceProvider.getAccountId(), maxPhotoCount)
    }

    override suspend fun uploadPhoto(
        photoFile: File,
        photoUri: Uri,
        extension: String,
        photoKey: String?
    ): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
//            val mediaType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
//            if (mediaType == null) {
//                if (photoKey != null) {
//                    photoDAO.updateStatusBy(photoKey, PhotoStatus.UPLOAD_ERROR)
//                }
//                return@withContext Resource.error(PhotoNotSupportedTypeException())
//            }

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


//            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?.let { mimeType ->
//                val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
//                val photo = createPhoto(accountId, photoKey, photoUri, extension)
//
//                val uploadPhotoToS3Response = uploadPhotoToS3(photoFile, photo, mimeType)
//                if (uploadPhotoToS3Response.isError()) {
//                    return@withContext uploadPhotoToS3Response
//                }
//
//                return@withContext savePhoto(photo)
//            } ?: return@withContext Resource.error(PhotoNotSupportedTypeException())
        }
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


//        val uploadPhotoToS3Response = getPreSignedURLResponse.data?.let { preSignedURL ->
//            uploadPhotoToS3(photoFile, photo.key, mimeType, preSignedURL.url, preSignedURL.fields)
//        } ?: kotlin.run {
//            if (getPreSignedURLResponse.isError()) {
//                val photoAlreadyExists = getPreSignedURLResponse.isExceptionCodeEqualTo(ExceptionCode.PHOTO_ALREADY_EXIST_EXCEPTION)
//                val photoStatus = if (photoAlreadyExists) {
//                    PhotoStatus.OCCUPIED
//                } else {
//                    PhotoStatus.UPLOAD_ERROR
//                }
//                photoDAO.updateStatusBy(photo.key, photoStatus)
//            }
//            return getPreSignedURLResponse.toEmptyResponse()
//        }
//
//        if (uploadPhotoToS3Response.isError()) {
//            photoDAO.updateStatusBy(photo.key, PhotoStatus.UPLOAD_ERROR)
//        } else if (uploadPhotoToS3Response.isSuccess()) {
//            photoDAO.updateUploadedBy(photo.key, true)
//        }
//        return uploadPhotoToS3Response
    }

    private suspend fun savePhoto(photo: Photo): Resource<EmptyResponse> {
        if (photo.saved) {
            photoDAO.updateStatusBy(photo.key, PhotoStatus.OCCUPIED)
            return Resource.success(EmptyResponse())
        }
        val savePhotoResponse = photoRDS.savePhoto(photo.key, photo.sequence)

        if (savePhotoResponse.isError()) {
            val photoAlreadyExists = savePhotoResponse.isExceptionCodeEqualTo(ExceptionCode.PHOTO_ALREADY_EXIST_EXCEPTION)
            if (photoAlreadyExists) {
                photoDAO.updateStatusBy(photo.key, PhotoStatus.OCCUPIED)
            } else {
                photoDAO.updateStatusBy(photo.key, PhotoStatus.UPLOAD_ERROR)
            }
        } else if (savePhotoResponse.isSuccess()) photoDAO.updateAsSavedBy(photo.key)

        return savePhotoResponse
    }

    private suspend fun uploadPhotoToS3(
        photoFile: File,
        photoKey: String,
        mimeType: String,
        url: String,
        fields: Map<String, String>
    ): Resource<EmptyResponse> {
        val formData = mutableMapOf<String, RequestBody>()
        for ((key, value) in fields) {
            formData[key] = RequestBody.create(MultipartBody.FORM, value)
        }
        val requestBody = RequestBody.create(MediaType.parse(mimeType), photoFile)
        val multiPartBody = MultipartBody.Part.createFormData(FILE, photoKey, requestBody)
        return photoRDS.uploadPhotoToS3(url, formData, multiPartBody)
    }

    private suspend fun createPhoto(accountId: UUID, photoKey: String?, photoUri: Uri, extension: String): Photo {
        val photo = photoDAO.getBy(photoKey)?.let { photo ->
            photo.status = PhotoStatus.UPLOADING
            photo
        } ?: kotlin.run {
            val sequence = (photoDAO.getLastSequenceBy(accountId) ?: 0) + 1
            val newPhotoKey = UUID.randomUUID().toString() + "." + extension
            Photo(newPhotoKey, accountId, PhotoStatus.UPLOADING, photoUri, sequence, sequence, false, false)
        }
        photoDAO.insert(photo)
        return photo

//        return photoKey?.let { key ->
//            val photo = photoDAO.getBy(key)
//            photo?.status = PhotoStatus.UPLOADING
//            photo
//        } ?: kotlin.run {
//            val sequence = (photoDAO.getLastSequenceBy(accountId) ?: 0) + 1
//            val newPhotoKey = UUID.randomUUID().toString() + "." + extension
//            Photo(newPhotoKey, accountId, PhotoStatus.UPLOADING, photoUri, sequence, sequence, false, false)
//        }
    }

    override suspend fun listPhotos(maxPhotoCount: Int): List<Photo> {
        return withContext(ioDispatcher) {
            return@withContext photoDAO.getAllBy(preferenceProvider.getAccountId(), maxPhotoCount)
        }
    }

    override suspend fun deletePhoto(photoKey: String): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val photo = photoDAO.getBy(photoKey) ?: return@withContext Resource.error(PhotoNotExistException())
            val response = if (photo.uploaded || photo.saved) {
                photoRDS.deletePhoto(photoKey)
            } else {
                Resource.success(EmptyResponse())
            }
            if (response.isSuccess()) {
                deletePhoto(photo.uri)
                photoDAO.deleteBy(photo.key)
            } else {
                if (photo.uploaded && photo.saved) {
                    photoDAO.updateStatusBy(photoKey, PhotoStatus.DOWNLOADING)
                } else {
                    photoDAO.updateStatusBy(photoKey, PhotoStatus.UPLOAD_ERROR)
                }
            }
            return@withContext response
        }
    }

    private fun deletePhoto(photoUri: Uri?) {
        photoUri?.path?.let { path ->
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    override suspend fun updatePhotoStatus(photoKey: String, photoStatus: PhotoStatus) {
        withContext(ioDispatcher) {
            photoDAO.updateStatusBy(photoKey, photoStatus)
        }
    }

    override suspend fun orderPhotos(photoSequences: Map<String, Int>): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val photos = photoDAO.getAllBy(preferenceProvider.getAccountId(), PhotoConstant.MAX_NUM_OF_PHOTOS).toMutableList()

            for (i in photos.size - 1 downTo 0) {
                val photo = photos[i]
                val sequence = photoSequences[photo.key]
                if (sequence != null && photo.sequence != sequence) {
                    photo.status = PhotoStatus.ORDERING
                    photo.oldSequence = photo.sequence
                    photo.sequence = sequence
                } else {
                    photos.removeAt(i)
                }
            }
            if (photos.isEmpty()) {
                return@withContext Resource.success(EmptyResponse())
            }
            photoDAO.insert(photos)
            return@withContext doOrderPhotos(photos, photoSequences)
        }
    }

    override suspend fun reorderPhotos(photos: List<Photo>): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            if (photos.isEmpty()) {
                return@withContext Resource.success(EmptyResponse())
            }
            val photoSequences = photos.map { photo ->
                photo.key to photo.sequence
            }.toMap()
            return@withContext doOrderPhotos(photos, photoSequences)
        }
    }

    private suspend fun doOrderPhotos(photos: List<Photo>, photoSequences: Map<String, Int>): Resource<EmptyResponse> {
        val response = photoRDS.orderPhotos(photoSequences)
        photos.forEach { photo ->
            photo.status = PhotoStatus.OCCUPIED
            if (response.isError()) {
                photo.sequence = photo.oldSequence
            }
        }
        photoDAO.insert(photos)
        return response
    }

    override suspend fun deletePhotos() {
        withContext(ioDispatcher) {
            val photos = photoDAO.getAllBy(preferenceProvider.getAccountId(), Int.MAX_VALUE)
            photos.forEach { photo ->
                photoDAO.deleteBy(photo.key)
                deletePhoto(photo.uri)
            }
        }
    }

    override fun getProfilePhotoKeyFlow(): Flow<String?> {
        return photoDAO.getProfilePhotoKeyFlowBy(preferenceProvider.getAccountId())
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