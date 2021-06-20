package com.beeswork.balance.ui.setting.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.setting.SettingRepository

class EmailSettingViewModelFactory(
    private val settingRepository: SettingRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EmailSettingViewModel(settingRepository) as T
    }
}