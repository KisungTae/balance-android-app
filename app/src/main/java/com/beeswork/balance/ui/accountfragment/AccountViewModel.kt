package com.beeswork.balance.ui.accountfragment

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.domain.uistate.profile.ProfileUIState
import com.beeswork.balance.domain.usecase.account.FetchProfileUseCase
import com.beeswork.balance.domain.usecase.login.GetEmailUseCase
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AccountViewModel(
    private val photoRepository: PhotoRepository,
    private val fetchProfileUseCase: FetchProfileUseCase,
    private val getEmailUseCase: GetEmailUseCase,
    private val profileMapper: ProfileMapper,
    private val preferenceProvider: PreferenceProvider
) : BaseViewModel() {

    private val _profileUIStateLiveData = MutableLiveData<ProfileUIState>()
    val profileUIStateLiveData: LiveData<ProfileUIState> = _profileUIStateLiveData

    private val _emailLiveData = MutableLiveData<String?>()
    val emailLiveData: LiveData<String?> = _emailLiveData

    val profilePhotoURLLiveData by lazyDeferred {
        photoRepository.getProfilePhotoFlow().map { profilePhoto ->
            EndPoint.ofPhoto(preferenceProvider.getPhotoDomain(), profilePhoto?.accountId, profilePhoto?.key)
        }.asLiveData()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            val response = fetchProfileUseCase.invoke(false)
            if (response.isSuccess() && response.data != null) {
                _profileUIStateLiveData.postValue(profileMapper.toProfileUIState(response.data))
            }
        }
    }

    fun fetchEmail() {
        viewModelScope.launch {
            _emailLiveData.postValue(getEmailUseCase.invoke())
        }
    }

    fun fetchPhotos() {
//        viewModelScope.launch { photoRepository.fetchPhotos() }
    }
}