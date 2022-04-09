package com.beeswork.balance.domain.usecase.photo

import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.internal.constant.EndPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class GetProfilePhotoUrlUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): GetProfilePhotoUrlUseCase {

    override suspend fun invoke(): String? = withContext(defaultDispatcher){
        try {
            val profilePhoto = photoRepository.getProfilePhoto() ?: return@withContext null
            return@withContext EndPoint.ofPhoto(profilePhoto.accountId, profilePhoto.key)
        } catch (e: IOException) {
            return@withContext null
        }
    }
}