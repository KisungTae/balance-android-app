package com.beeswork.balance.ui.accountfragment

import androidx.lifecycle.*
import com.beeswork.balance.domain.uistate.profile.ProfileUIState
import com.beeswork.balance.domain.usecase.account.FetchProfileUseCase
import com.beeswork.balance.domain.usecase.account.GetProfilePhotoFlowUseCase
import com.beeswork.balance.domain.usecase.login.GetEmailUseCase
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AccountViewModel(
    private val fetchProfileUseCase: FetchProfileUseCase,
    private val getEmailUseCase: GetEmailUseCase,
    private val getProfilePhotoFlowUseCase: GetProfilePhotoFlowUseCase,
    private val profileMapper: ProfileMapper
) : BaseViewModel() {

    private val _profileUIStateLiveData = MutableLiveData<ProfileUIState>()
    val profileUIStateLiveData: LiveData<ProfileUIState> = _profileUIStateLiveData

    private val _emailLiveData = MutableLiveData<String?>()
    val emailLiveData: LiveData<String?> = _emailLiveData

    val profilePhotoURLLiveData by lazyDeferred {
        getProfilePhotoFlowUseCase.invoke().map { profilePhoto ->
            EndPoint.ofPhoto(profilePhoto?.accountId, profilePhoto?.key)
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