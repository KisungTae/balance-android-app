package com.beeswork.balance.data.network.rds.photo

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.DeletePhotoBody
import com.beeswork.balance.data.network.request.OrderPhotosBody
import com.beeswork.balance.data.network.request.SavePhotoBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.data.network.response.photo.PreSignedURLDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

class PhotoRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), PhotoRDS {

    override suspend fun orderPhotos(
        accountId: UUID,
        identityToken: UUID,
        photoSequences: Map<String, Int>
    ): Resource<EmptyResponse> {
        return getResult { balanceAPI.orderPhotos(OrderPhotosBody(accountId, identityToken, photoSequences)) }
    }


    override suspend fun savePhoto(
        accountId: UUID,
        identityToken: UUID,
        photoKey: String,
        sequence: Int
    ): Resource<EmptyResponse> {
        return getResult { balanceAPI.savePhoto(SavePhotoBody(accountId, identityToken, photoKey, sequence)) }
    }

    override suspend fun uploadPhotoToS3(
        url: String,
        formData: Map<String, RequestBody>,
        multipartBody: MultipartBody.Part
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.uploadPhotoToS3(url, formData, multipartBody)
        }
    }

    override suspend fun getPreSignedURL(
        accountId: UUID,
        identityToken: UUID,
        photoKey: String
    ): Resource<PreSignedURLDTO> {
        return getResult {
            balanceAPI.getPreSignedURL(accountId, identityToken, photoKey)
        }
    }


    override suspend fun deletePhoto(
        accountId: UUID,
        identityToken: UUID,
        photoKey: String
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.deletePhoto(DeletePhotoBody(accountId, identityToken, photoKey))
        }
    }

    override suspend fun fetchPhotos(accountId: UUID, identityToken: UUID): Resource<List<PhotoDTO>> {
        return getResult {
            balanceAPI.fetchPhotos(accountId, identityToken)
        }
    }
}