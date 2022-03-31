package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.exception.AboutMaxSizeExceedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class SaveAboutUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveAboutUseCase {

    override suspend fun invoke(about: String): Resource<EmptyResponse> = withContext(defaultDispatcher) {
        try {
            if (about.toByteArray().size > MAX_ABOUT_SIZE) {
                return@withContext Resource.error(AboutMaxSizeExceedException())
            }
            if (about.isBlank()) {
                return@withContext Resource.success(EmptyResponse())
            }
            profileRepository.saveAbout(about)
            return@withContext Resource.success(EmptyResponse())
        } catch (e: IOException) {
            return@withContext Resource.error(e)
        }
    }

    companion object {
        const val MAX_ABOUT_SIZE = 500
    }
}