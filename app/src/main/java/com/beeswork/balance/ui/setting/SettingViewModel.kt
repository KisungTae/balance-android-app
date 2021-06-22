package com.beeswork.balance.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import kotlinx.coroutines.launch

class SettingViewModel(
    private val settingRepository: SettingRepository
): ViewModel() {
}