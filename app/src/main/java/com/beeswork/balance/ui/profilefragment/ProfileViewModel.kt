package com.beeswork.balance.ui.profilefragment

import androidx.lifecycle.*
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.domain.uistate.profile.FetchProfileUIState
import com.beeswork.balance.domain.uistate.profile.ProfileUIState
import com.beeswork.balance.domain.uistate.profile.SaveBioUIState
import com.beeswork.balance.domain.usecase.account.FetchProfileUseCase
import com.beeswork.balance.domain.usecase.profile.SaveBioUseCase
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val fetchProfileUseCase: FetchProfileUseCase,
    private val saveBioUseCase: SaveBioUseCase,
    private val profileMapper: ProfileMapper
) : BaseViewModel() {

    private val _fetchProfileUIStateLiveData = MutableLiveData<FetchProfileUIState>()
    val fetchProfileUIStateLiveData: LiveData<FetchProfileUIState> get() = _fetchProfileUIStateLiveData

    private val _saveBioLiveData = MutableLiveData<SaveBioUIState>()
    val saveBioLiveData: LiveData<SaveBioUIState> get() = _saveBioLiveData

    fun fetchProfile() {
        viewModelScope.launch {
            _fetchProfileUIStateLiveData.postValue(FetchProfileUIState.ofLoading())
            val response = fetchProfileUseCase.invoke(true)
            val fetchProfileUIState = if (response.isSuccess() && response.data != null) {
                val profileUIState = profileMapper.toProfileUIState(response.data)
                FetchProfileUIState.ofSuccess(profileUIState)
            } else {
                FetchProfileUIState.ofError(response.exception)
            }
            _fetchProfileUIStateLiveData.postValue(fetchProfileUIState)
        }
    }

    fun saveBio(height: Int?, about: String?) {
        viewModelScope.launch {
            _saveBioLiveData.postValue(SaveBioUIState.ofLoading())
            val response = saveBioUseCase.invoke(height, about)
            val saveBioUIState = if (response.isSuccess()) {
                SaveBioUIState.ofSuccess()
            } else {
                SaveBioUIState.ofError(response.exception)
            }
            _saveBioLiveData.postValue(saveBioUIState)
        }
    }

}





