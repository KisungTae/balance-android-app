package com.beeswork.balance.ui.account

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.internal.util.safeLaunch
import kotlinx.coroutines.launch

class AccountViewModel(
    private val settingRepository: SettingRepository
): ViewModel() {

    val emailLiveData by lazyDeferred {
        settingRepository.getEmailFlow().asLiveData()
    }
}