package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import java.io.IOException

class SaveBirthDateUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveBirthDateUseCase {

    override suspend fun invoke(year: Int, month: Int, day: Int): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                profileRepository.saveBirthDate(LocalDate.of(year, month, day))
                return@withContext Resource.success(EmptyResponse())
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }
}