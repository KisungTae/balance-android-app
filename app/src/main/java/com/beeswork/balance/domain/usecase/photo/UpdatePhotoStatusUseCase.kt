package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.internal.constant.PhotoStatus

interface UpdatePhotoStatusUseCase {

    suspend fun invoke(photoKey: String, photoStatus: PhotoStatus)
}