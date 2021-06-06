package com.beeswork.balance.data.network.rds.photo

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.data.network.response.photo.PreSignedURLDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

interface PhotoRDS {

    suspend fun savePhoto(
        accountId: UUID?,
        identityToken: UUID?,
        photoKey: String,
        sequence: Int
    ): Resource<EmptyResponse>

    suspend fun uploadPhotoToS3(
        url: String,
        formData: Map<String, RequestBody>,
        multipartBody: MultipartBody.Part
    ): Resource<EmptyResponse>

    suspend fun getPreSignedURL(
        accountId: UUID?,
        identityToken: UUID?,
        photoKey: String
    ): Resource<PreSignedURLDTO>

    suspend fun deletePhoto(
        accountId: UUID?,
        identityToken: UUID?,
        photoKey: String
    ): Resource<EmptyResponse>

    suspend fun listPhotos(
        accountId: UUID?,
        identityToken: UUID?
    ): Resource<List<PhotoDTO>>
}