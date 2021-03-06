package com.beeswork.balance.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository

class AccountViewModelFactory(
    private val settingRepository: SettingRepository,
    private val photoRepository: PhotoRepository,
    private val profileRepository: ProfileRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AccountViewModel(settingRepository, photoRepository, profileRepository) as T
    }
}