package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class GetAboutUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : GetAboutUseCase {

    override suspend fun invoke(): String? {
        return try {
            withContext(defaultDispatcher) {
                return@withContext profileRepository.getAbout()
            }
        } catch (e: IOException) {
            return null
        }
    }
}