package com.beeswork.balance.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.ui.setting.push.PushSettingViewModel

class SettingViewModelFactory(
    private val settingRepository: SettingRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingViewModel(settingRepository) as T
    }
}