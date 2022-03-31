package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class GetHeightUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : GetHeightUseCase {

    override suspend fun invoke(): Int? = withContext(defaultDispatcher) {
        try {
            return@withContext profileRepository.getHeight()
        } catch (e: IOException) {
            return@withContext null
        }
    }
}