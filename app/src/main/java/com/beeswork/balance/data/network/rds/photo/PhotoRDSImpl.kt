package com.beeswork.balance.data.network.rds.photo

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.profile.DeletePhotoBody
import com.beeswork.balance.data.network.request.profile.OrderPhotosBody
import com.beeswork.balance.data.network.request.profile.SavePhotoBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.data.network.response.photo.PreSignedURLDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

class PhotoRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), PhotoRDS {

    override suspend fun orderPhotos(photoSequences: Map<String, Int>): Resource<EmptyResponse> {
        return getResult { balanceAPI.orderPhotos(OrderPhotosBody(photoSequences)) }
    }

    override suspend fun savePhoto(photoKey: String, sequence: Int): Resource<EmptyResponse> {
        return getResult { balanceAPI.savePhoto(SavePhotoBody(photoKey, sequence)) }
    }

    override suspend fun uploadPhotoToS3(
        url: String,
        formData: Map<String, RequestBody>,
        multipartBody: MultipartBody.Part
    ): Resource<EmptyResponse> {
        return getResult { balanceAPI.uploadPhotoToS3(url, formData, multipartBody) }
    }

    override suspend fun getPreSignedURL(photoKey: String): Resource<PreSignedURLDTO> {
        return getResult { balanceAPI.getPreSignedURL(photoKey) }
    }

    override suspend fun deletePhoto(photoKey: String): Resource<EmptyResponse> {
        return getResult { balanceAPI.deletePhoto(DeletePhotoBody(photoKey)) }
    }

    override suspend fun fetchPhotos(): Resource<List<PhotoDTO>> {
        return getResult { balanceAPI.fetchPhotos() }
    }
}