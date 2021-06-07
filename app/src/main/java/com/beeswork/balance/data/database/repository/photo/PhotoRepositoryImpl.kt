package com.beeswork.balance.data.database.repository.photo

import android.net.Uri
import android.nfc.tech.MifareUltralight
import android.webkit.MimeTypeMap
import com.beeswork.balance.data.database.dao.PhotoDAO
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.rds.photo.PhotoRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.*

class PhotoRepositoryImpl(
    private val photoRDS: PhotoRDS,
    private val photoDAO: PhotoDAO,
    private val photoMapper: PhotoMapper,
    private val preferenceProvider: PreferenceProvider,
    private val ioDispatcher: CoroutineDispatcher
) : PhotoRepository {

    override suspend fun fetchPhotos(): Resource<List<Photo>> {
        return withContext(ioDispatcher) {
//            if (photoDAO.existsBySynced(false) || photoDAO.count() <= 0) {
            if (photoDAO.count() <= 0) {
                val response = photoRDS.listPhotos(
                    preferenceProvider.getAccountId(),
                    preferenceProvider.getIdentityToken()
                )
//                response.data?.let { photoDTOs ->
//                    val photos = photoDTOs.map { photoDTO -> photoMapper.toPhoto(photoDTO) }
//                    photos.forEach { photo -> photo.synced = true }
//                    photos.sortedBy { photo -> photo.sequence }
//                    photoDAO.insert(photos)
//                    return@withContext response.mapData(photos)
//                }
//                return@withContext response.mapData(listOf<Photo>())
            }
            return@withContext Resource.success(photoDAO.findAll(4))
        }
    }

    override fun getPhotosFlow(maxPhotoCount: Int): Flow<List<Photo>> {
        return photoDAO.findAllAsFlow(maxPhotoCount)
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
        if (photo.uploaded) return Resource.success(null)

        val getPreSignedURLResponse = photoRDS.getPreSignedURL(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            photo.key
//            "720ff7cc-4ec8-48d7-89a2-d190df74b5cd.jpg"
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
            return Resource.success(null)
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

        formData["Content-Type"] = RequestBody.create(MultipartBody.FORM, "")

        val requestBody = RequestBody.create(MediaType.parse(mimeType), photoFile)
        val multiPartBody = MultipartBody.Part.createFormData(FILE, photoKey, requestBody)
        val response = photoRDS.uploadPhotoToS3(url, formData, multiPartBody)

        if (response.isSuccess()) deletePhoto(photoFile)
        return response
    }

    private fun deletePhoto(photoFile: File) {
        try {
            photoFile.delete()
        } catch (e: IOException) {
            // TODO: log exception?
        } catch (e: SecurityException) {
            // TODO: log exception?
        }
    }


    private fun createPhoto(photoKey: String?, photoUri: Uri, extension: String): Photo {
        return photoKey?.let { key ->
            val photo = photoDAO.findByKey(key)
            photo?.status = PhotoStatus.UPLOADING
            photo
        } ?: kotlin.run {
            val sequence = (photoDAO.findLastSequence() ?: 0) + 1
            val newPhotoKey = UUID.randomUUID().toString() + "." + extension
            Photo(newPhotoKey, PhotoStatus.UPLOADING, photoUri, sequence, sequence, false, false)
        }
    }


    override suspend fun loadPhotos(maxPhotoCount: Int): List<Photo> {
        return withContext(Dispatchers.IO) {
            return@withContext photoDAO.findAll(maxPhotoCount)
        }
    }

    override suspend fun deletePhoto(photoKey: String): Resource<EmptyResponse> {
//        return withContext(Dispatchers.IO) {
//            photoDAO.findByKey(photoKey)?.let { photo ->
//                if (!photo.photoCreated && !photo.photoUploaded)
//
//            } ?: return@withContext Resource.success(EmptyResponse())
//        }
        return Resource.success(null)
    }

    override suspend fun updatePhotoStatus(photoKey: String, photoStatus: PhotoStatus) {
        withContext(Dispatchers.IO) {
            photoDAO.updateStatus(photoKey, photoStatus)
        }
    }

    override suspend fun test() {
        withContext(ioDispatcher) {
            photoDAO.insert(Photo("", PhotoStatus.EMPTY, null, 100, 100, false, false))
        }
    }

    companion object {
        const val FILE = "file"
    }
}