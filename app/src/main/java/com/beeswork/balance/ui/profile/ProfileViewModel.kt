package com.beeswork.balance.ui.profile

import android.net.Uri
import androidx.lifecycle.*
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.internal.util.safeLaunch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val photoRepository: PhotoRepository,
    private val photoMapper: PhotoMapper,
    private val profileMapper: ProfileMapper
) : ViewModel() {

    val photos by lazyDeferred {
        photoRepository.getPhotosFlow().map { photos ->
            val photoPickers = mutableListOf<PhotoPicker>()
            photos.map { photo -> photoPickers.add(photoMapper.toPhotoPicker(photo)) }
            repeat((MAX_PHOTO_COUNT - photos.size)) {
                photoPickers.add(PhotoPicker.asEmpty())
            }
            photoPickers
        }.asLiveData()
    }

    private val _fetchProfileLiveData = MutableLiveData<ProfileDomain>()
    val fetchProfileLiveData: LiveData<ProfileDomain> get() = _fetchProfileLiveData

    private val _saveAboutLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveAboutLiveData: LiveData<Resource<EmptyResponse>> get() = _saveAboutLiveData

    private val _fetchPhotosLiveData = MutableLiveData<Resource<List<PhotoPicker>>>()
    val fetchPhotosLiveData: LiveData<Resource<List<PhotoPicker>>> get() = _fetchPhotosLiveData

    fun fetchProfile() {
        viewModelScope.launch {
            profileRepository.fetchProfile()?.let { profile ->
                if (!profile.synced) saveAbout(profile.height, profile.about)
                _fetchProfileLiveData.postValue(profileMapper.toProfileDomain(profile))
            }
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

    fun addPhoto(photoUri: Uri?) {
        photoUri?.path?.let { path ->

        }
    }

    fun test() {
        profileRepository.test()
    }

    companion object {
        const val MAX_PHOTO_COUNT = 6
    }
}