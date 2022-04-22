package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.database.entity.photo.Photo

interface GetProfilePhotoUseCase {

    suspend fun invoke(): Photo?
}