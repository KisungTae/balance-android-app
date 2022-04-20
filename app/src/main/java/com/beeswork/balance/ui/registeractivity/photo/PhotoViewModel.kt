package com.beeswork.balance.ui.registeractivity.photo

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.domain.uistate.photo.SyncPhotosUIState
import com.beeswork.balance.domain.usecase.photo.*
import com.beeswork.balance.internal.constant.PhotoConstant
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
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): BaseViewModel() {

    val photoItemUIStateLiveData by viewModelLazyDeferred {
        photoRepository.getPhotosFlow(PhotoConstant.MAX_NUM_OF_PHOTOS).map { photos ->

        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    private val _syncPhotosUIStateLiveData = MutableLiveData<SyncPhotosUIState>()
    val syncPhotosUIStateLiveData: LiveData<SyncPhotosUIState> get() = _syncPhotosUIStateLiveData


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


}