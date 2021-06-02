package com.beeswork.balance.data.database.repository.photo

import android.net.Uri
import android.webkit.MimeTypeMap
import com.beeswork.balance.data.database.converter.OffsetDateTimeConverter
import com.beeswork.balance.data.database.dao.PhotoDAO
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.rds.photo.PhotoRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class PhotoRepositoryImpl(
    private val photoRDS: PhotoRDS,
    private val photoDAO: PhotoDAO,
    private val photoMapper: PhotoMapper,
    private val preferenceProvider: PreferenceProvider
) : PhotoRepository {

    override suspend fun fetchPhotos(): Resource<List<Photo>> {
        return withContext(Dispatchers.IO) {
            if (photoDAO.existsBySynced(false) || photoDAO.count() <= 0) {
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
            return@withContext Resource.success(photoDAO.findAll())
        }
    }

    override suspend fun getPhotosFlow(maxPhotoCount: Int): Flow<List<Photo>> {
        return withContext(Dispatchers.IO) {
            return@withContext photoDAO.findAllAsFlow(maxPhotoCount)
        }
    }

    override suspend fun uploadPhoto(photoFile: File, photoUri: Uri, extension: String): Resource<EmptyResponse> {
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)?.let { mimeType ->
            val sequence = photoDAO.findLastSequence() ?: 0
            val photo = Photo(UUID.randomUUID(), PhotoStatus.UPLOADING, photoUri, (sequence + 1), false)
            photoDAO.insert(photo)


            return Resource.success(null)
        } ?: return Resource.error(ExceptionCode.PHOTO_NOT_SUPPORTED_TYPE_EXCEPTION)
    }
}