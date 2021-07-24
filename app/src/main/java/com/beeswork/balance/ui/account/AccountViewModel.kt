package com.beeswork.balance.ui.account

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel

class AccountViewModel(
    private val settingRepository: SettingRepository,
    private val photoRepository: PhotoRepository,
    private val profileRepository: ProfileRepository
): BaseViewModel() {

    val emailLiveData by viewModelLazyDeferred {
        settingRepository.getEmailFlow().asLiveData()
    }

    val profilePhotoKeyLiveData by lazyDeferred {
        photoRepository.getProfilePhotoKeyFlow().asLiveData()
    }

    val nameLiveData by lazyDeferred {
        profileRepository.getNameFlow().asLiveData()
    }
}