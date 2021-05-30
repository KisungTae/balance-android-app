package com.beeswork.balance.data.database.repository.photo

import com.beeswork.balance.data.database.dao.PhotoDAO
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.rds.photo.PhotoRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
                response.data?.let { photoDTOs ->
                    val photos = photoDTOs.map { photoDTO -> photoMapper.toPhoto(photoDTO) }
                    photos.forEach { photo -> photo.synced = true }
                    photos.sortedBy { photo -> photo.sequence }
                    photoDAO.insert(photos)
                    return@withContext response.mapData(photos)
                }
                return@withContext response.mapData(listOf<Photo>())
            }
            return@withContext Resource.success(photoDAO.findAll())
        }
    }
}