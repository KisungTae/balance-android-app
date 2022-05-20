package com.beeswork.balance.ui.profilefragment

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.*
import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.domain.uistate.profile.ProfileUIState
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.internal.exception.PhotoNotExistException
import com.beeswork.balance.internal.exception.PhotoNotSupportedTypeException
import com.beeswork.balance.internal.exception.PhotoOverSizeException
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.ui.photofragment.PhotoItemUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val photoRepository: PhotoRepository,
    private val photoMapper: PhotoMapper,
    private val profileMapper: ProfileMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val _fetchProfileLiveData = MutableLiveData<Resource<ProfileUIState>>()
    val fetchProfileLiveData: LiveData<Resource<ProfileUIState>> get() = _fetchProfileLiveData

    private val _saveBioLiveData = MutableLiveData<Resource<ProfileUIState>>()
    val saveBioLiveData: LiveData<Resource<ProfileUIState>> get() = _saveBioLiveData

    fun fetchProfile() {
        viewModelScope.launch {
            val profile = profileRepository.getProfile()
            val isProfileSynced = profile?.synced == true
            val profileDomain = profile?.let { _profile -> profileMapper.toProfileUIState(_profile) }

            if (isProfileSynced)
                _fetchProfileLiveData.postValue(Resource.success(profileDomain))
            else {
                _fetchProfileLiveData.postValue(Resource.loading(profileDomain))
                val response = profileRepository.fetchProfile(true).map {
                    it?.let { _profile -> profileMapper.toProfileUIState(_profile) }
                }
                _fetchProfileLiveData.postValue(response)
            }
        }
    }

    fun saveBio(height: Int?, about: String) {
        viewModelScope.launch {
            _saveBioLiveData.postValue(Resource.loading())
            val response = profileRepository.saveBio(height, about).map {
                it?.let { profile -> profileMapper.toProfileUIState(profile) }
            }
            _saveBioLiveData.postValue(response)
        }
    }

}





