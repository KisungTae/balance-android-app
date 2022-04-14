package com.beeswork.balance.ui.registeractivity.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.uistate.UIState
import com.beeswork.balance.domain.usecase.photo.FetchPhotosUseCase
import kotlinx.coroutines.launch

open class PhotoViewModel (
    private val fetchPhotosUseCase: FetchPhotosUseCase
): ViewModel() {

    private val _fetchPhotosUIStateLiveData = MutableLiveData<UIState>()
    val fetchPhotosUIStateLiveData: LiveData<UIState> get() = _fetchPhotosUIStateLiveData


    fun fetchPhotos() {
        viewModelScope.launch {
            _fetchPhotosUIStateLiveData.postValue(UIState.ofLoading())
            val response = fetchPhotosUseCase.invoke()
            if (response.isError()) {
                _fetchPhotosUIStateLiveData.postValue(UIState.ofError(response.exception))
            }
        }
    }
}