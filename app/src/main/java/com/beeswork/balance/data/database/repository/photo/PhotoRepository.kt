package com.beeswork.balance.data.database.repository.photo

import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.response.Resource
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    suspend fun fetchPhotos(): Resource<List<Photo>>
    suspend fun getPhotosFlow(): Flow<List<Photo>>
}