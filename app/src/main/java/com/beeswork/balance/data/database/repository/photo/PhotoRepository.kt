package com.beeswork.balance.data.database.repository.photo

import android.net.Uri
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.PhotoStatus
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.*

interface PhotoRepository {
    suspend fun fetchPhotos(): Resource<EmptyResponse>
    fun getPhotosFlow(maxPhotoCount: Int): Flow<List<Photo>>
    suspend fun uploadPhoto(photoFile: File, photoUri: Uri, extension: String, photoKey: String?): Resource<EmptyResponse>
    suspend fun listPhotos(maxPhotoCount: Int): List<Photo>
    suspend fun deletePhoto(photoKey: String): Resource<EmptyResponse>
    suspend fun updatePhotoStatus(photoKey: String, photoStatus: PhotoStatus)
    suspend fun orderPhotos(photoSequences: Map<String, Int>): Resource<EmptyResponse>
    suspend fun deletePhotos()
    fun getProfilePhotoKeyFlow(): Flow<String?>
    suspend fun test()
}