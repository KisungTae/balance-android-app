package com.beeswork.balance.data.database.repository.photo

import android.net.Uri
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PhotoRepository {
    suspend fun fetchPhotos(): Resource<List<Photo>>
    fun getPhotosFlow(maxPhotoCount: Int): Flow<List<Photo>>
    suspend fun uploadPhoto(photoFile: File, photoUri: Uri, extension: String): Resource<EmptyResponse>
    suspend fun test()
}