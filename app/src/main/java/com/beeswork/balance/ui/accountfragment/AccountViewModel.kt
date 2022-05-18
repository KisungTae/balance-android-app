package com.beeswork.balance.ui.accountfragment

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.domain.usecase.account.FetchProfileUseCase
import com.beeswork.balance.domain.usecase.login.GetEmailUseCase
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class AccountViewModel(
    private val photoRepository: PhotoRepository,
    private val fetchProfileUseCase: FetchProfileUseCase,
    private val getEmailUseCase: GetEmailUseCase
) : BaseViewModel() {



    val profilePhotoKeyLiveData by lazyDeferred {
//        photoRepository.getProfilePhotoKeyFlow().asLiveData()
    }

//    val nameLiveData by lazyDeferred {
//        profileRepository.getNameFlow().asLiveData()
//    }

    fun fetchProfile() {
        viewModelScope.launch {
            fetchProfileUseCase.invoke(false)
        }
    }

    fun fetchPhotos() {
//        viewModelScope.launch { photoRepository.fetchPhotos() }
    }
}