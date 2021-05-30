package com.beeswork.balance.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.internal.util.safeLaunch
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val photoRepository: PhotoRepository,
    private val photoMapper: PhotoMapper,
    private val profileMapper: ProfileMapper
) : ViewModel() {

    private val _fetchProfileLiveData = MutableLiveData<ProfileDomain>()
    val fetchProfileLiveData: LiveData<ProfileDomain> get() = _fetchProfileLiveData

    private val _saveAboutLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveAboutLiveData: LiveData<Resource<EmptyResponse>> get() = _saveAboutLiveData

    private val _fetchPhotosLiveData = MutableLiveData<Resource<List<PhotoPicker>>>()
    val fetchPhotosLiveData: LiveData<Resource<List<PhotoPicker>>> get() = _fetchPhotosLiveData

    fun fetchProfile() {
        viewModelScope.launch {
            _fetchProfileLiveData.postValue(profileMapper.toProfileDomain(profileRepository.fetchProfile()))
        }
    }

    fun saveAbout(height: Int?, about: String) {
        viewModelScope.safeLaunch(_saveAboutLiveData) {
            _saveAboutLiveData.postValue(Resource.loading())
            _saveAboutLiveData.postValue(profileRepository.saveAbout(height, about))
        }
    }

    fun fetchPhotos() {
        viewModelScope.safeLaunch(_fetchPhotosLiveData) {
//            _fetchPhotosLiveData.postValue(Resource.loading())
            val response = photoRepository.fetchPhotos().let {
                it.mapData(it.data?.map { photo -> photoMapper.toPhotoPicker(photo) })
            }
            _fetchPhotosLiveData.postValue(response)
        }
    }

    fun test() {
        profileRepository.test()
    }
}