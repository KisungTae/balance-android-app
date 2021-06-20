package com.beeswork.balance.ui.setting.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.setting.SettingRepository

class NotificationSettingViewModelFactory(
    private val settingRepository: SettingRepository
):  ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationSettingViewModel(settingRepository) as T
    }
}