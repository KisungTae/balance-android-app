package com.beeswork.balance.data.database.repository.photo

import android.net.Uri
import android.webkit.MimeTypeMap
import com.beeswork.balance.data.database.dao.PhotoDAO
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.rds.photo.PhotoRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.photo.PreSignedURLDTO
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
        photoKey: UUID?
    ): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?.let { mimeType ->
                val photo = createPhoto(photoKey, photoUri)
                photoDAO.insert(photo)

                val getPreSignedURLResponse = getPreSignedURL(photo.key)
                if (getPreSignedURLResponse.isError()) return@withContext getPreSignedURLResponse.toEmptyResponse()

                val uploadPhotoToS3Response = getPreSignedURLResponse.data?.let { preSignedURL ->
                    uploadPhotoToS3(photoFile, photo.key, mimeType, preSignedURL.url, preSignedURL.fields)
                } ?: Resource.error(ExceptionCode.CONNECT_EXCEPTION)

                if (uploadPhotoToS3Response.isError()) return@withContext uploadPhotoToS3Response
                else if (uploadPhotoToS3Response.isSuccess()) photoDAO.updateUploaded(photo.key, true)

                val savePhotoResponse = savePhoto(photo.key, photo.sequence)
                if (savePhotoResponse.isError()) return@withContext savePhotoResponse
                else if (savePhotoResponse.isSuccess()) photoDAO.updateOnPhotoSaved(photo.key)
                return@withContext savePhoto(photo.key, photo.sequence)


//                else getPreSignedURLResponse.data?.let {
//                    val uploadPhotoToS3Response = uploadPhotoToS3(photoFile, photo.key, mimeType, it.url, it.fields)
//                    if (uploadPhotoToS3Response.isError()) return@withContext uploadPhotoToS3Response
//                    else if (uploadPhotoToS3Response.isSuccess()) {
//                        photoDAO.updatePhotoUploaded(photo.key, true)
//                        return@withContext photoRDS.savePhoto(
//                            preferenceProvider.getAccountId(),
//                            preferenceProvider.getIdentityToken(),
//                            photo.key,
//                            photo.sequence
//                        )
//                    }
//                }
//                return@withContext Resource.success(null)
            } ?: return@withContext Resource.error(ExceptionCode.PHOTO_NOT_SUPPORTED_TYPE_EXCEPTION)
        }
    }

    private suspend fun getPreSignedURL(photoKey: UUID): Resource<PreSignedURLDTO> {
        return photoRDS.getPreSignedURL(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            photoKey
        )
    }

    private suspend fun uploadPhotoToS3(
        photo: File,
        photoKey: UUID,
        mimeType: String,
        url: String,
        fields: Map<String, String>
    ): Resource<EmptyResponse> {
        val formData = mutableMapOf<String, RequestBody>()
        for ((key, value) in fields) {
            formData[key] = RequestBody.create(MultipartBody.FORM, value)
        }
        val requestBody = RequestBody.create(MediaType.parse(mimeType), photo)
        val multiPartBody = MultipartBody.Part.createFormData(FILE, photoKey.toString(), requestBody)
        return photoRDS.uploadPhotoToS3(url, formData, multiPartBody)
    }

    private suspend fun savePhoto(photoKey: UUID, sequence: Int): Resource<EmptyResponse> {
        return photoRDS.savePhoto(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            photoKey,
            sequence
        )
    }

    private fun createPhoto(photoKey: UUID?, photoUri: Uri): Photo {
        return photoKey?.let { key ->
            val photo = photoDAO.findByKey(key)
            photo?.status = PhotoStatus.UPLOADING
            photo
        } ?: kotlin.run {
            val sequence = (photoDAO.findLastSequence() ?: 0) + 1
            Photo(UUID.randomUUID(), PhotoStatus.UPLOADING, photoUri, sequence, sequence, false, false)
        }
    }


    override suspend fun loadPhotos(maxPhotoCount: Int): List<Photo> {
        return withContext(Dispatchers.IO) {
            return@withContext photoDAO.findAll(maxPhotoCount)
        }
    }

    override suspend fun deletePhoto(photoKey: UUID): Resource<EmptyResponse> {
//        return withContext(Dispatchers.IO) {
//            photoDAO.findByKey(photoKey)?.let { photo ->
//                if (!photo.photoCreated && !photo.photoUploaded)
//
//            } ?: return@withContext Resource.success(EmptyResponse())
//        }
        return Resource.success(null)
    }

    override suspend fun updatePhotoStatus(photoKey: UUID, photoStatus: PhotoStatus) {
        withContext(Dispatchers.IO) {
            photoDAO.updateStatus(photoKey, photoStatus)
        }
    }

    override suspend fun test() {
        withContext(ioDispatcher) {
            photoDAO.insert(Photo(UUID.randomUUID(), PhotoStatus.EMPTY, null, 100, 100, false, false))
        }
    }

    companion object {
        const val FILE = "file"
    }
}