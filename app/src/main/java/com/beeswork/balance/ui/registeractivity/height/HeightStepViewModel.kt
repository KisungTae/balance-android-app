package com.beeswork.balance.ui.registeractivity.height

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.uistate.register.RegisterStepUIState
import com.beeswork.balance.domain.usecase.register.GetHeightUseCase
import com.beeswork.balance.domain.usecase.register.SaveHeightUseCase
import kotlinx.coroutines.launch

class HeightStepViewModel (
    private val getHeightUseCase: GetHeightUseCase,
    private val saveHeightUseCase: SaveHeightUseCase
): ViewModel() {

    private val _heightLiveData = MutableLiveData<Int?>()
    val heightLiveData: LiveData<Int?> get() = _heightLiveData

    private val _saveHeightUIStateLiveData = MutableLiveData<RegisterStepUIState>()
    val saveHeightUIStateLiveData: LiveData<RegisterStepUIState> get() = _saveHeightUIStateLiveData

    fun getHeight() {
        viewModelScope.launch {
            _heightLiveData.postValue(getHeightUseCase.invoke())
        }
    }

    fun saveHeight(height: Int) {
        viewModelScope.launch {
            val response = saveHeightUseCase.invoke(height)
            val saveHeightUIState = if (response.isSuccess()) {
                RegisterStepUIState.ofSuccess()
            } else {
                RegisterStepUIState.ofError(response.exception)
            }
            _saveHeightUIStateLiveData.postValue(saveHeightUIState)
        }
    }
}