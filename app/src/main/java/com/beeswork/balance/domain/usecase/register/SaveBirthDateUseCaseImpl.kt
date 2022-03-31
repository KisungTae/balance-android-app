package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import java.io.IOException

class SaveBirthDateUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveBirthDateUseCase {

    override suspend fun invoke(year: Int, month: Int, day: Int): Resource<EmptyResponse> = withContext(defaultDispatcher) {
        try {
            val birthDate = OffsetDateTime.of(year, month, day, 0, 0, 0, 0, ZoneOffset.UTC)
            profileRepository.saveBirthDate(birthDate)
            return@withContext Resource.success(EmptyResponse())
        } catch (e: IOException) {
            return@withContext Resource.error(e)
        }
    }
}