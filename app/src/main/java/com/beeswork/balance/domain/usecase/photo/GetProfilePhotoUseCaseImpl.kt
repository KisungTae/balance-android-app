package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.internal.constant.EndPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class GetProfilePhotoUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : GetProfilePhotoUseCase {

    override suspend fun invoke(): Photo? = withContext(defaultDispatcher) {
        try {
            return@withContext photoRepository.getProfilePhoto()
        } catch (e: IOException) {
            return@withContext null
        }
    }
}