package com.beeswork.balance.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.internal.util.safeLaunch
import kotlinx.coroutines.launch

class AccountViewModel(
    private val settingRepository: SettingRepository
): ViewModel() {

    private val _emailLiveData = MutableLiveData<String?>()
    val emailLiveData: LiveData<String?> get() = _emailLiveData

    fun fetchEmail() {
        viewModelScope.safeLaunch<Any>(null) {
            _emailLiveData.postValue(settingRepository.fetchEmail())
        }
    }

}