package com.beeswork.balance.ui.account

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.internal.exception.AccountNotFoundException
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class AccountViewModel(
    private val settingRepository: SettingRepository,
    private val photoRepository: PhotoRepository,
    private val profileRepository: ProfileRepository,
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    val emailLiveData by viewModelLazyDeferred {
        loginRepository.getEmailFlow().asLiveData()
    }

    val profilePhotoKeyLiveData by lazyDeferred {
        photoRepository.getProfilePhotoKeyFlow().asLiveData()
    }

    val nameLiveData by lazyDeferred {
        profileRepository.getNameFlow().asLiveData()
    }

    fun fetchEmail() {
        viewModelScope.launch(coroutineExceptionHandler) { loginRepository.fetchEmail() }
    }

    fun fetchSetting() {
        viewModelScope.launch(coroutineExceptionHandler) { settingRepository.fetchSetting() }
    }

    fun fetchProfile() {
        viewModelScope.launch(coroutineExceptionHandler) { profileRepository.fetchProfile() }
    }

    fun fetchPhotos() {
        viewModelScope.launch(coroutineExceptionHandler) { photoRepository.fetchPhotos() }
    }
}