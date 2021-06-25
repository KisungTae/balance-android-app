package com.beeswork.balance.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.internal.util.lazyDeferred
import kotlinx.coroutines.launch

class SettingViewModel(
    private val settingRepository: SettingRepository
): ViewModel() {

    val email by lazyDeferred { settingRepository.getEmailFlow().asLiveData() }

    fun fetchEmail() {
        viewModelScope.launch { settingRepository.fetchEmail() }
    }
}