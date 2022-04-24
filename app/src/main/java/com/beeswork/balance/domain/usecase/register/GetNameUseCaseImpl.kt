package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class GetNameUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : GetNameUseCase {

    override suspend fun invoke(): String? {
        return try {
            withContext(defaultDispatcher) {
                return@withContext profileRepository.getName()
            }
        } catch (e: IOException) {
            null
        }
    }
}