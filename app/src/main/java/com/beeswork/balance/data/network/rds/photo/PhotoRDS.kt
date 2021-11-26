package com.beeswork.balance.data.network.rds.photo

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.data.network.response.photo.PreSignedURLDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface PhotoRDS {

    suspend fun orderPhotos(photoSequences: Map<String, Int>): Resource<EmptyResponse>
    suspend fun savePhoto(photoKey: String, sequence: Int): Resource<EmptyResponse>

    suspend fun uploadPhotoToS3(
        url: String,
        formData: Map<String, RequestBody>,
        multipartBody: MultipartBody.Part
    ): Resource<EmptyResponse>

    suspend fun getPreSignedURL(photoKey: String): Resource<PreSignedURLDTO>
    suspend fun deletePhoto(photoKey: String): Resource<EmptyResponse>
    suspend fun fetchPhotos(): Resource<List<PhotoDTO>>
}