package com.beeswork.balance.data.database.repository.photo

import android.net.Uri
import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.PhotoStatus
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PhotoRepository {
    suspend fun getProfilePhoto(): Photo?
    fun getPhotosFlow(maxPhotoCount: Int): Flow<List<Photo>>
    suspend fun fetchPhotos(maxNumOfPhotos: Int): Resource<List<Photo>>
    suspend fun uploadPhoto(photoFile: File, photoUri: Uri, extension: String, photoKey: String?): Resource<EmptyResponse>

    suspend fun deletePhoto(photoKey: String): Resource<EmptyResponse>
    suspend fun cancelDeletePhoto(photoKey: String)
    suspend fun updatePhotoStatus(photoKey: String, photoStatus: PhotoStatus)
    suspend fun updatePhotoStatuses(photoKeys: List<String>, photoStatus: PhotoStatus)
    suspend fun orderPhotos(photoSequences: Map<String, Int>): Resource<EmptyResponse>
    suspend fun cancelOrderPhotos(photoKeys: List<String>)
    suspend fun deletePhotos()

    suspend fun test()
}