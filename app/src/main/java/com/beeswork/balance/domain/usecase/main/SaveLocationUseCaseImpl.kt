package com.beeswork.balance.domain.usecase.main

import com.beeswork.balance.data.database.repository.setting.SettingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveLocationUseCaseImpl(
    private val settingRepository: SettingRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveLocationUseCase {
    override suspend fun invoke(latitude: Double, longitude: Double, syncLocation: Boolean) = withContext(defaultDispatcher) {
        settingRepository.saveLocation(latitude, longitude, syncLocation)
    }
}