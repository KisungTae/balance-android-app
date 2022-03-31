package com.beeswork.balance.ui.registeractivity.height

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.domain.uistate.register.SaveProfileUIState
import com.beeswork.balance.domain.usecase.register.GetHeightUseCase
import com.beeswork.balance.domain.usecase.register.SaveHeightUseCase
import kotlinx.coroutines.launch

class HeightViewModel (
    private val getHeightUseCase: GetHeightUseCase,
    private val saveHeightUseCase: SaveHeightUseCase
): ViewModel() {

    private val _heightLiveData = MutableLiveData<Int?>()
    val heightLiveData: LiveData<Int?> get() = _heightLiveData

    private val _saveHeightUIStateLiveData = MutableLiveData<SaveProfileUIState>()
    val saveHeightUIStateLiveData: LiveData<SaveProfileUIState> get() = _saveHeightUIStateLiveData

    fun getHeight() {
        viewModelScope.launch {
            _heightLiveData.postValue(getHeightUseCase.invoke())
        }
    }

    fun saveHeight(height: Int) {
        viewModelScope.launch {
            val response = saveHeightUseCase.invoke(height)
            val saveHeightUIState = if (response.isSuccess()) {
                SaveProfileUIState.ofSuccess()
            } else {
                SaveProfileUIState.ofError(response.exception)
            }
            _saveHeightUIStateLiveData.postValue(saveHeightUIState)
        }
    }
}