package com.beeswork.balance.data.database.repository.photo

import android.net.Uri
import android.webkit.MimeTypeMap
import com.beeswork.balance.data.database.dao.PhotoDAO
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.rds.photo.PhotoRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.PhotoConstant
import com.beeswork.balance.internal.constant.PhotoStatus
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

    override suspend fun fetchPhotos(): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            if (photoDAO.count(preferenceProvider.getAccountId()) <= 0) {
                val response = photoRDS.fetchPhotos(
                    preferenceProvider.getAccountId(),
                    preferenceProvider.getIdentityToken()
                )
                response.data?.let { photoDTOs ->
                    val photos = photoDTOs.map { photoDTO -> photoMapper.toPhoto(photoDTO) }
                    photos.forEach { photo -> photo.synced = true }
                    photos.sortedBy { photo -> photo.sequence }
                    photoDAO.insert(photos)
                }
                return@withContext response.toEmptyResponse()
            }
            return@withContext Resource.success(EmptyResponse())
        }
    }

    override fun getPhotosFlow(maxPhotoCount: Int): Flow<List<Photo>> {
        return photoDAO.findAllAsFlow(preferenceProvider.getAccountId(), maxPhotoCount)
    }

    override suspend fun uploadPhoto(
        photoFile: File,
        photoUri: Uri,
        extension: String,
        photoKey: String?
    ): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?.let { mimeType ->
                val photo = createPhoto(photoKey, photoUri, extension)
                photoDAO.insert(photo)

                val uploadPhotoToS3Response = uploadPhotoToS3(photoFile, photo, mimeType)
                if (uploadPhotoToS3Response.isError()) return@withContext uploadPhotoToS3Response

                return@withContext savePhoto(photo)
            } ?: return@withContext Resource.error(ExceptionCode.PHOTO_NOT_SUPPORTED_TYPE_EXCEPTION)
        }
    }

    private suspend fun uploadPhotoToS3(photoFile: File, photo: Photo, mimeType: String): Resource<EmptyResponse> {
        if (photo.uploaded) return Resource.success(EmptyResponse())

        val getPreSignedURLResponse = photoRDS.getPreSignedURL(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            photo.key
        )

        if (getPreSignedURLResponse.isError()) {
            val photoAlreadyExists = getPreSignedURLResponse.error == ExceptionCode.PHOTO_ALREADY_EXIST_EXCEPTION
            val photoStatus = if (photoAlreadyExists) PhotoStatus.OCCUPIED else PhotoStatus.UPLOAD_ERROR
            photoDAO.updateStatus(photo.key, photoStatus)
            return getPreSignedURLResponse.toEmptyResponse()
        }

        val uploadPhotoToS3Response = getPreSignedURLResponse.data?.let { preSignedURL ->
            uploadPhotoToS3(photoFile, photo.key, mimeType, preSignedURL.url, preSignedURL.fields)
        } ?: Resource.error(ExceptionCode.BAD_REQUEST_EXCEPTION)

        if (uploadPhotoToS3Response.isError()) photoDAO.updateStatus(photo.key, PhotoStatus.UPLOAD_ERROR)
        else if (uploadPhotoToS3Response.isSuccess()) photoDAO.updateUploaded(photo.key, true)
        return uploadPhotoToS3Response
    }

    private suspend fun savePhoto(photo: Photo): Resource<EmptyResponse> {
        if (photo.saved) {
            photoDAO.updateStatus(photo.key, PhotoStatus.OCCUPIED)
            return Resource.success(EmptyResponse())
        }

        val savePhotoResponse = photoRDS.savePhoto(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            photo.key,
            photo.sequence
        )

        if (savePhotoResponse.isError()) {
            val photoAlreadyExists = savePhotoResponse.error == ExceptionCode.PHOTO_ALREADY_EXIST_EXCEPTION
            if (photoAlreadyExists) photoDAO.updateStatus(photo.key, PhotoStatus.OCCUPIED)
            else photoDAO.updateStatus(photo.key, PhotoStatus.UPLOAD_ERROR)
        } else if (savePhotoResponse.isSuccess()) photoDAO.updateOnPhotoSaved(photo.key)

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

    private fun createPhoto(photoKey: String?, photoUri: Uri, extension: String): Photo {
        return photoKey?.let { key ->
            val photo = photoDAO.findByKey(key)
            photo?.status = PhotoStatus.UPLOADING
            photo
        } ?: kotlin.run {
            val sequence = (photoDAO.findLastSequence(preferenceProvider.getAccountId()) ?: 0) + 1
            val newPhotoKey = UUID.randomUUID().toString() + "." + extension
            val accountId = preferenceProvider.getAccountId()
            Photo(newPhotoKey, accountId, PhotoStatus.UPLOADING, photoUri, sequence, sequence, false, false)
        }
    }


    override suspend fun listPhotos(maxPhotoCount: Int): List<Photo> {
        return withContext(ioDispatcher) {
            return@withContext photoDAO.findAll(preferenceProvider.getAccountId(), maxPhotoCount)
        }
    }

    override suspend fun deletePhoto(photoKey: String): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            photoDAO.findByKey(photoKey)?.let { photo ->
                val response = if (photo.uploaded || photo.saved) photoRDS.deletePhoto(
                    preferenceProvider.getAccountId(),
                    preferenceProvider.getIdentityToken(),
                    photo.key
                ) else Resource.success(EmptyResponse())

                if (response.isSuccess()) {
                    deletePhoto(photo.uri)
                    photoDAO.deletePhoto(photo.key)
                }
                return@withContext response
            } ?: return@withContext Resource.error(ExceptionCode.PHOTO_NOT_EXIST_EXCEPTION)
        }
    }

    private fun deletePhoto(photoUri: Uri) {
        try {
            photoUri.path?.let { path ->
                val file = File(path)
                if (file.exists()) file.delete()
            }
        } catch (e: IOException) {
            // TODO: log exception?
        } catch (e: SecurityException) {
            // TODO: log exception?
        }
    }

    override suspend fun updatePhotoStatus(photoKey: String, photoStatus: PhotoStatus) {
        withContext(ioDispatcher) {
            photoDAO.updateStatus(photoKey, photoStatus)
        }
    }

    override suspend fun orderPhotos(photoSequences: Map<String, Int>): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val photos = photoDAO.findAll(
                preferenceProvider.getAccountId(),
                PhotoConstant.MAX_PHOTO_COUNT
            ).toMutableList()

            for (i in photos.size - 1 downTo 0) {
                val photo = photos[i]
                if (photo.status != PhotoStatus.OCCUPIED)
                    return@withContext Resource.error(ExceptionCode.PHOTO_NOT_ORDERABLE_EXCEPTION)

                photoSequences[photo.key]?.let { sequence ->
                    if (photo.sequence != sequence) {
                        photo.status = PhotoStatus.ORDERING
                        photo.oldSequence = photo.sequence
                        photo.sequence = sequence
                    } else photos.removeAt(i)
                } ?: kotlin.run { photos.removeAt(i) }
            }
            if (photos.size <= 0) return@withContext Resource.success(EmptyResponse())

            photoDAO.insert(photos)
            val response = photoRDS.orderPhotos(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                photoSequences
            )

            photos.forEach { photo ->
                photo.status = PhotoStatus.OCCUPIED
                if (response.isError()) photo.sequence = photo.oldSequence
            }
            photoDAO.insert(photos)
            return@withContext response
        }
    }

    override suspend fun deletePhotos() {
        withContext(ioDispatcher) {
            val photos = photoDAO.findAll(preferenceProvider.getAccountId(), Int.MAX_VALUE)
            photos.forEach { photo ->
                photoDAO.deletePhoto(photo.key)
                deletePhoto(photo.uri)
            }
        }
    }

    override fun getProfilePhotoKeyFlow(): Flow<String?> {
        return photoDAO.findProfilePhotoKeyAsFlow(preferenceProvider.getAccountId())
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