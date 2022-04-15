package com.beeswork.balance.domain.usecase.photo

import android.net.Uri
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface UploadPhotoUseCase {

    suspend fun invoke(photoUri: Uri?, photoKey: String?): Resource<EmptyResponse>
}