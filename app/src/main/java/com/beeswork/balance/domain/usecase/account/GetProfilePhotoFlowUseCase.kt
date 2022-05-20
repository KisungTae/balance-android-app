package com.beeswork.balance.domain.usecase.account

import com.beeswork.balance.data.database.entity.photo.Photo
import kotlinx.coroutines.flow.Flow

interface GetProfilePhotoFlowUseCase {

    suspend fun invoke(): Flow<Photo?>
}