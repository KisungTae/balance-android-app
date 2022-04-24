package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class GetGenderUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : GetGenderUseCase {

    override suspend fun invoke(): Boolean? {
        return try {
            withContext(defaultDispatcher) {
                return@withContext profileRepository.getGender()
            }
        } catch (e: IOException) {
            null
        }
    }
}