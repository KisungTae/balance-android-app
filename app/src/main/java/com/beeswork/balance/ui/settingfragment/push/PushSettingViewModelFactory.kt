package com.beeswork.balance.ui.settingfragment.push

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.internal.mapper.setting.PushSettingMapper

class PushSettingViewModelFactory(
    private val settingRepository: SettingRepository,
    private val pushSettingMapper: PushSettingMapper
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PushSettingViewModel(settingRepository, pushSettingMapper) as T
    }
}