package com.beeswork.balance.ui.profile

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.*
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.ui.profile.photo.PhotoPicker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val photoRepository: PhotoRepository,
    private val photoMapper: PhotoMapper,
    private val profileMapper: ProfileMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _fetchProfileLiveData = MutableLiveData<ProfileDomain>()
    val fetchProfileLiveData: LiveData<ProfileDomain> get() = _fetchProfileLiveData

    private val _saveAboutLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveAboutLiveData: LiveData<Resource<EmptyResponse>> get() = _saveAboutLiveData

    private val _fetchPhotosLiveData = MutableLiveData<Resource<List<PhotoPicker>>>()
    val fetchPhotosLiveData: LiveData<Resource<List<PhotoPicker>>> get() = _fetchPhotosLiveData

    private val _uploadPhotoLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val uploadPhotoLiveData: LiveData<Resource<EmptyResponse>> get() = _uploadPhotoLiveData

    private val _deletePhotoLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val deletePhotoLiveData: LiveData<Resource<EmptyResponse>> get() = _deletePhotoLiveData

    private val _orderPhotosLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val orderPhotosLiveData: LiveData<Resource<EmptyResponse>> get() = _deletePhotoLiveData

    private val _syncPhotosLiveData = MutableLiveData<Boolean>()
    val syncPhotosLiveData: LiveData<Boolean> get() = _syncPhotosLiveData

    fun fetchProfile() {
        viewModelScope.launch {
            profileRepository.fetchProfile()?.let { profile ->
                if (!profile.synced) saveAbout(profile.height, profile.about)
                _fetchProfileLiveData.postValue(profileMapper.toProfileDomain(profile))
            }
        }
    }

    fun saveAbout(height: Int?, about: String) {
        viewModelScope.launch {
            _saveAboutLiveData.postValue(Resource.loading())
            _saveAboutLiveData.postValue(profileRepository.saveAbout(height, about))
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
//            photos.map { photo -> photoPickers.add(photoMapper.toPhotoPicker(photo)) }
//            repeat((MAX_PHOTO_COUNT - photos.size)) { photoPickers.add(PhotoPicker.asEmpty()) }
            photoPickers
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    fun uploadPhoto(photoUri: Uri?, photoKey: String?) {
        viewModelScope.launch {
            photoUri?.path?.let { path ->
                val photoFile = File(path)
                val extension = MimeTypeMap.getFileExtensionFromUrl(path)
                if (validatePhoto(photoFile, extension)) {
                    val response = photoRepository.uploadPhoto(photoFile, photoUri, extension, photoKey)
                    _uploadPhotoLiveData.postValue(response)
                } else photoKey?.let { key -> photoRepository.updatePhotoStatus(key, PhotoStatus.UPLOAD_ERROR) }
            } ?: _uploadPhotoLiveData.postValue(Resource.error(ExceptionCode.PHOTO_NOT_EXIST_EXCEPTION))
        }
    }

    private fun validatePhoto(photoFile: File, extension: String): Boolean {
        if (!photoFile.exists()) {
            _uploadPhotoLiveData.postValue(Resource.error(ExceptionCode.PHOTO_NOT_EXIST_EXCEPTION))
            return false
        }
        if (photoFile.length() > MAX_SIZE) {
            _uploadPhotoLiveData.postValue(Resource.error(ExceptionCode.PHOTO_OVER_SIZE_EXCEPTION))
            return false
        }
        if (!photoExtensions.contains(extension)) {
            _uploadPhotoLiveData.postValue(Resource.error(ExceptionCode.PHOTO_NOT_SUPPORTED_TYPE_EXCEPTION))
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

    private fun orderPhotos(photoSequences: Map<String, Int>) {
        viewModelScope.launch {
            _orderPhotosLiveData.postValue(photoRepository.orderPhotos(photoSequences))
        }
    }

    fun syncPhotos() {
        viewModelScope.launch(Dispatchers.Default) {
            val photos = photoRepository.loadPhotos(MAX_PHOTO_COUNT)
            val photoSequences = mutableMapOf<String, Int>()
            photos.forEach { photo ->
                when (photo.status) {
                    PhotoStatus.OCCUPIED, PhotoStatus.DOWNLOAD_ERROR -> downloadPhoto(photo.key)
                    PhotoStatus.UPLOAD_ERROR, PhotoStatus.UPLOADING -> reuploadPhoto(photo)
                    PhotoStatus.DELETING -> deletePhoto(photo.key)
                    PhotoStatus.ORDERING -> photoSequences[photo.key] = photo.sequence
                    else -> println("")
                }
            }
            if (photoSequences.isNotEmpty()) orderPhotos(photoSequences)
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





