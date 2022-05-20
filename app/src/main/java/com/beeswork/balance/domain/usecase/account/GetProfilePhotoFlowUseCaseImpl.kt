package com.beeswork.balance.domain.usecase.account

import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GetProfilePhotoFlowUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : GetProfilePhotoFlowUseCase {
    override suspend fun invoke(): Flow<Photo?> = withContext(defaultDispatcher) {
        return@withContext photoRepository.getProfilePhotoFlow()
    }
}