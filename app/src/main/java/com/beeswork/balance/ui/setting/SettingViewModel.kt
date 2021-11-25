package com.beeswork.balance.ui.setting

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.location.LocationMapper
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingViewModel(
    private val settingRepository: SettingRepository,
    private val chatRepository: ChatRepository,
    private val clickRepository: ClickRepository,
    private val matchRepository: MatchRepository,
    private val photoRepository: PhotoRepository,
    private val swipeRepository: SwipeRepository,
    private val profileRepository: ProfileRepository,
    private val loginRepository: LoginRepository,
    private val locationMapper: LocationMapper
) : BaseViewModel() {

    private val _deleteAccountLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val deleteAccountLiveData: LiveData<Resource<EmptyResponse>> get() = _deleteAccountLiveData

    val emailLiveData by viewModelLazyDeferred { loginRepository.getEmailFlow().asLiveData() }

    val locationLiveData by viewModelLazyDeferred {
        settingRepository.getLocationFlow().map { location ->
            location?.let { _location -> locationMapper.toLocationDomain(_location) }
        }.asLiveData()
    }

    fun fetchEmail() {
        viewModelScope.launch {
            if (!loginRepository.isEmailSynced()) loginRepository.fetchEmail()
        }
    }

    fun fetchSetting() {
        viewModelScope.launch { settingRepository.fetchSettings() }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _deleteAccountLiveData.postValue(Resource.loading())
            val response = settingRepository.deleteAccount()
            if (response.isSuccess()) {
                chatRepository.deleteChatMessages()
                clickRepository.deleteClicks()
                settingRepository.deleteSettings()
                matchRepository.deleteMatches()
                photoRepository.deletePhotos()
                swipeRepository.deleteSwipes()
                profileRepository.deleteProfile()
                loginRepository.deleteLogin()
            }
            _deleteAccountLiveData.postValue(response)
        }
    }
}