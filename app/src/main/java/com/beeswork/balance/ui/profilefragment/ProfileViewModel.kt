package com.beeswork.balance.ui.profilefragment

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.*
import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.internal.exception.PhotoNotExistException
import com.beeswork.balance.internal.exception.PhotoNotSupportedTypeException
import com.beeswork.balance.internal.exception.PhotoOverSizeException
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.ui.profilefragment.photo.PhotoPicker
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

    private val _fetchProfileLiveData = MutableLiveData<Resource<ProfileDomain>>()
    val fetchProfileLiveData: LiveData<Resource<ProfileDomain>> get() = _fetchProfileLiveData

    private val _saveBioLiveData = MutableLiveData<Resource<ProfileDomain>>()
    val saveBioLiveData: LiveData<Resource<ProfileDomain>> get() = _saveBioLiveData

    private val _fetchPhotosLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val fetchPhotosLiveData: LiveData<Resource<EmptyResponse>> get() = _fetchPhotosLiveData

    private val _uploadPhotoLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val uploadPhotoLiveData: LiveData<Resource<EmptyResponse>> get() = _uploadPhotoLiveData

    private val _deletePhotoLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val deletePhotoLiveData: LiveData<Resource<EmptyResponse>> get() = _deletePhotoLiveData

    private val _orderPhotosLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val orderPhotosLiveData: LiveData<Resource<EmptyResponse>> get() = _orderPhotosLiveData

    private val _syncPhotosLiveData = MutableLiveData<Boolean>()
    val syncPhotosLiveData: LiveData<Boolean> get() = _syncPhotosLiveData

    fun fetchProfile() {
        viewModelScope.launch {
            val profile = profileRepository.getProfile()
            val isProfileSynced = profile?.synced == true
            val profileDomain = profile?.let { _profile -> profileMapper.toProfileDomain(_profile) }

            if (isProfileSynced)
                _fetchProfileLiveData.postValue(Resource.success(profileDomain))
            else {
                _fetchProfileLiveData.postValue(Resource.loading(profileDomain))
                val response = profileRepository.fetchProfile().map {
                    it?.let { _profile -> profileMapper.toProfileDomain(_profile) }
                }
                _fetchProfileLiveData.postValue(response)
            }
        }
    }

    fun saveBio(height: Int?, about: String) {
        viewModelScope.launch {
            _saveBioLiveData.postValue(Resource.loading())
            val response = profileRepository.saveBio(height, about).map {
                it?.let { profile -> profileMapper.toProfileDomain(profile) }
            }
            _saveBioLiveData.postValue(response)
        }
    }








    fun fetchPhotos() {
        viewModelScope.launch {
            _fetchPhotosLiveData.postValue(Resource.loading())
            _fetchPhotosLiveData.postValue(photoRepository.fetchPhotos())
        }
    }

    fun getPhotosLiveData(): LiveData<MutableMap<String, PhotoPicker>> {
        return photoRepository.getPhotosFlow(MAX_PHOTO_COUNT).map { photos ->
            val photoPickers = mutableMapOf<String, PhotoPicker>()
            photos.mapIndexed { index, photo ->
                val photoPicker = photoMapper.toPhotoPicker(photo)
                photoPicker.sequence = index
                photoPickers[photo.key] = photoPicker
            }
            photoPickers
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    fun uploadPhoto(photoUri: Uri?, photoKey: String?) {
        viewModelScope.launch { doUploadPhoto(photoUri, photoKey) }
    }

    private suspend fun doUploadPhoto(photoUri: Uri?, photoKey: String?) {
        photoUri?.path?.let { path ->
            val photoFile = File(path)
            val extension = MimeTypeMap.getFileExtensionFromUrl(path)
            if (validatePhoto(photoFile, extension)) {
                val response = photoRepository.uploadPhoto(photoFile, photoUri, extension, photoKey)
                _uploadPhotoLiveData.postValue(response)
            } else photoKey?.let { key -> photoRepository.updatePhotoStatus(key, PhotoStatus.UPLOAD_ERROR) }
        } ?: _uploadPhotoLiveData.postValue(Resource.error(PhotoNotExistException()))
    }

    private fun validatePhoto(photoFile: File, extension: String): Boolean {
        if (!photoFile.exists()) {
            _uploadPhotoLiveData.postValue(Resource.error(PhotoNotExistException()))
            return false
        }
        if (photoFile.length() > MAX_SIZE) {
            _uploadPhotoLiveData.postValue(Resource.error(PhotoOverSizeException()))
            return false
        }
        if (!photoExtensions.contains(extension)) {
            _uploadPhotoLiveData.postValue(Resource.error(PhotoNotSupportedTypeException()))
            return false
        }
        return true
    }

    fun onDownloadPhotoError(photoKey: String?) {
        viewModelScope.launch {
            photoKey?.let { key -> photoRepository.updatePhotoStatus(key, PhotoStatus.DOWNLOAD_ERROR) }
        }
    }

    fun onDownloadPhotoSuccess(photoKey: String?) {
        viewModelScope.launch {
            photoKey?.let { key -> photoRepository.updatePhotoStatus(key, PhotoStatus.OCCUPIED) }
        }
    }

    fun orderPhotos(photoSequences: Map<String, Int>) {
        viewModelScope.launch {
            val response = photoRepository.orderPhotos(photoSequences)
            _orderPhotosLiveData.postValue(response)
        }
    }

    fun syncPhotos() {
        viewModelScope.launch(defaultDispatcher) {
            val photos = photoRepository.listPhotos(MAX_PHOTO_COUNT)
            val photoSequences = mutableMapOf<String, Int>()
            photos.forEach { photo ->
                when (photo.status) {
                    PhotoStatus.OCCUPIED, PhotoStatus.DOWNLOAD_ERROR -> launch {
                        photoRepository.updatePhotoStatus(photo.key, PhotoStatus.DOWNLOADING)
                    }
                    PhotoStatus.UPLOAD_ERROR, PhotoStatus.UPLOADING -> launch {
                        doUploadPhoto(photo.uri, photo.key)
                    }
                    PhotoStatus.DELETING -> launch {
                        _deletePhotoLiveData.postValue(photoRepository.deletePhoto(photo.key))
                    }
                    PhotoStatus.ORDERING -> photoSequences[photo.key] = photo.sequence
                    else -> println("")
                }
            }
            if (photoSequences.isNotEmpty()) {
                launch { orderPhotos(photoSequences) }
            }
            _syncPhotosLiveData.postValue(true)
        }
    }

    fun downloadPhoto(photoKey: String?) {
        viewModelScope.launch {
            photoKey?.let { key -> photoRepository.updatePhotoStatus(key, PhotoStatus.DOWNLOADING) }
        }
    }

    fun deletePhoto(photoKey: String?) {
        viewModelScope.launch {
            photoKey?.let { key -> _deletePhotoLiveData.postValue(photoRepository.deletePhoto(key)) }
        }
    }

    private fun reuploadPhoto(photo: Photo) {
        uploadPhoto(photo.uri, photo.key)
    }


    fun test() {
//        profileRepository.test()
        viewModelScope.launch {
            repeat(1000) {
                println("viewmodelscope $it")
            }
//            photoRepository.test()
        }

    }

    companion object {
        private const val MAX_PHOTO_COUNT = 6
        private const val MAX_SIZE = 1048576
        private val photoExtensions = setOf("jpg", "jpeg", "gif", "png")
    }
}





