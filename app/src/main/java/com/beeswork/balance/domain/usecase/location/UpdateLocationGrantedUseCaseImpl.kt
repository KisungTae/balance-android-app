package com.beeswork.balance.domain.usecase.location

import com.beeswork.balance.data.database.repository.setting.SettingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateLocationGrantedUseCaseImpl(
    private val settingRepository: SettingRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : UpdateLocationGrantedUseCase {
    override suspend fun invoke(granted: Boolean) = withContext(defaultDispatcher) {
        settingRepository.updateLocationGranted(granted)
    }
}