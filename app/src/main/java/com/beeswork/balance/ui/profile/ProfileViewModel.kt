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
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.internal.util.safeLaunch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val photoRepository: PhotoRepository,
    private val photoMapper: PhotoMapper,
    private val profileMapper: ProfileMapper
) : ViewModel() {

    val photos by lazyDeferred {
        photoRepository.getPhotosFlow(MAX_PHOTO_COUNT).map { photos ->
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

    private val _uploadPhotoLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val uploadPhotoLiveData: LiveData<Resource<EmptyResponse>> get() = _uploadPhotoLiveData

    private var photosLiveDataInitialized = false

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

    fun testd(): LiveData<MutableList<PhotoPicker>> {
        return photoRepository.getPhotosFlow(MAX_PHOTO_COUNT).map { photos ->
            val photoPickers = mutableListOf<PhotoPicker>()
            photos.map { photo -> photoPickers.add(photoMapper.toPhotoPicker(photo)) }
            repeat((MAX_PHOTO_COUNT - photos.size)) {
                photoPickers.add(PhotoPicker.asEmpty())
            }
            photoPickers
        }.asLiveData()
    }

    suspend fun getPhotosLiveData(): LiveData<MutableList<PhotoPicker>> {


        viewModelScope.launch { }
        photoRepository.getPhotosFlow(4)
            .flowOn(Dispatchers.IO)
            .map { }
            .asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
        photosLiveDataInitialized = false
        return photoRepository.getPhotosFlow(MAX_PHOTO_COUNT).map { photos ->
//            if (!photosLiveDataInitialized) {
//                syncPhotos(photos)
//                photosLiveDataInitialized = true
//            }

            val photoPickers = mutableListOf<PhotoPicker>()
            photos.map { photo -> photoPickers.add(photoMapper.toPhotoPicker(photo)) }
//            repeat((MAX_PHOTO_COUNT - photos.size)) {
//                println("add empty photopickers $it")
//                photoPickers.add(PhotoPicker.asEmpty())
//            }
//            delay(5000)
            println("here end of map of photos")
            photoPickers
        }.asLiveData()
    }

    private fun syncPhotos(photos: List<Photo>) {
        repeat(1000) { index ->
            viewModelScope.launch {
                repeat(1000) {
                    println("sync photos $index")
                }
            }
        }

    }

    fun uploadPhoto(photoUri: Uri?) {
        viewModelScope.safeLaunch(_uploadPhotoLiveData) {
            photoUri?.path?.let { path ->
                val photoFile = File(path)
                val extension = MimeTypeMap.getFileExtensionFromUrl(path)
                if (validatePhoto(photoFile, extension))
                    _uploadPhotoLiveData.postValue(photoRepository.uploadPhoto(photoFile, photoUri, extension))
            }
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