package com.beeswork.balance.ui.photofragment

import android.net.Uri
import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.domain.uistate.photo.SyncPhotosUIState
import com.beeswork.balance.domain.usecase.photo.*
import com.beeswork.balance.internal.constant.PhotoConstant
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class PhotoViewModel (
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val orderPhotosUseCase: OrderPhotosUseCase,
    private val syncPhotosUseCase: SyncPhotosUseCase,
    private val updatePhotoStatusUseCase: UpdatePhotoStatusUseCase,
    private val photoRepository: PhotoRepository,
    private val photoMapper: PhotoMapper,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): BaseViewModel() {

    val photoItemUIStatesLiveData by viewModelLazyDeferred {
        photoRepository.getPhotosFlow(PhotoConstant.MAX_NUM_OF_PHOTOS).map { photos ->
            val photoItemUIStates = photos.map { photo ->
                photoMapper.toPhotoItemUIState(photo)
            }.toMutableList()

            repeat(PhotoConstant.MAX_NUM_OF_PHOTOS - photoItemUIStates.size) {
                photoItemUIStates.add(PhotoItemUIState.asEmpty())
            }
            photoItemUIStates
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    private val _syncPhotosUIStateLiveData = MutableLiveData<SyncPhotosUIState>()
    val syncPhotosUIStateLiveData: LiveData<SyncPhotosUIState> get() = _syncPhotosUIStateLiveData

    private val _uploadPhotoUIStateLiveData = MutableLiveData<UIState>()
    val uploadPhotoUIStateLiveData: LiveData<UIState> get() = _uploadPhotoUIStateLiveData

    private val _deletePhotoUIStateLiveData = MutableLiveData<UIState>()
    val deletePhotoUIStateLiveData: LiveData<UIState> get() = _deletePhotoUIStateLiveData

    private val _orderPhotosUIStateLiveData = MutableLiveData<UIState>()
    val orderPhotosUIStateLiveData: LiveData<UIState> get() = _orderPhotosUIStateLiveData


    fun syncPhotos() {
        viewModelScope.launch {
            _syncPhotosUIStateLiveData.postValue(SyncPhotosUIState.ofLoading())
            val response = syncPhotosUseCase.invoke()
            val syncPhotosUIState = if (response.isSuccess()) {
                SyncPhotosUIState.ofSuccess()
            } else {
                SyncPhotosUIState.ofError(response.exception)
            }
            _syncPhotosUIStateLiveData.postValue(syncPhotosUIState)
        }
    }

    fun updatePhotoStatus(photoKey: String?, photoStatus: PhotoStatus) {
        if (photoKey != null) {
            viewModelScope.launch {
                updatePhotoStatusUseCase.invoke(photoKey, photoStatus)
            }
        }
    }

    fun uploadPhoto(photoUri: Uri?, photoKey: String?) {
        viewModelScope.launch {
            val response = uploadPhotoUseCase.invoke(photoUri, photoKey)
            if (response.isError()) {
                _uploadPhotoUIStateLiveData.postValue(UIState.ofError(response.exception))
            }
        }
    }

    fun deletePhoto(photoKey: String?) {
        if (photoKey == null) {
            return
        }
        viewModelScope.launch {
            val response = deletePhotoUseCase.invoke(photoKey)
            if (response.isError()) {
                _deletePhotoUIStateLiveData.postValue(UIState.ofError(response.exception))
            }
        }
    }

    fun orderPhotos(photoSequences: Map<String, Int>) {
        if (photoSequences.isEmpty()) {
            return
        }
        viewModelScope.launch {
            val response = orderPhotosUseCase.invoke(photoSequences)
            if (response.isError()) {
                _orderPhotosUIStateLiveData.postValue(UIState.ofError(response.exception))
            }
        }
    }


}