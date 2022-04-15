package com.beeswork.balance.ui.registeractivity.photo

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.domain.uistate.photo.FetchPhotosUIState
import com.beeswork.balance.domain.usecase.photo.FetchPhotosUseCase
import com.beeswork.balance.internal.constant.PhotoConstant
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class PhotoViewModel (
    private val fetchPhotosUseCase: FetchPhotosUseCase,

    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): BaseViewModel() {

    val photoItemUIStateLiveData by viewModelLazyDeferred {
        photoRepository.getPhotosFlow(PhotoConstant.MAX_NUM_OF_PHOTOS).map { photos ->

        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    private val _fetchPhotosUIStateLiveData = MutableLiveData<FetchPhotosUIState>()
    val fetchPhotosUIStateLiveData: LiveData<FetchPhotosUIState> get() = _fetchPhotosUIStateLiveData


    fun fetchPhotos() {
        viewModelScope.launch {
            _fetchPhotosUIStateLiveData.postValue(FetchPhotosUIState.ofLoading())
            val response = fetchPhotosUseCase.invoke()
            val fetchPhotosUIState = if (response.isSuccess()) {
                FetchPhotosUIState.ofSuccess()
            } else {
                FetchPhotosUIState.ofError(response.exception)
            }
            _fetchPhotosUIStateLiveData.postValue(fetchPhotosUIState)
        }
    }


}