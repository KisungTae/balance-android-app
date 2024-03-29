package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import java.io.IOException

class GetBirthDateUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : GetBirthDateUseCase {

    override suspend fun invoke(): LocalDate? {
        return try {
            withContext(defaultDispatcher) {
                return@withContext profileRepository.getBirthDate()
            }
        } catch (e: IOException) {
            null
        }
    }
}