package com.beeswork.balance.ui.registeractivity.registerfinish

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.domain.uistate.register.RegisterStepUIState
import com.beeswork.balance.domain.uistate.register.SaveProfileUIState
import com.beeswork.balance.domain.usecase.register.SaveProfileUseCase
import kotlinx.coroutines.launch

class RegisterFinishViewModel (
    private val saveProfileUseCase: SaveProfileUseCase
): ViewModel() {

    private val _saveProfileUIStateLiveData = MutableLiveData<SaveProfileUIState>()
    val saveProfileUIStateLiveData: LiveData<SaveProfileUIState> get() = _saveProfileUIStateLiveData

    fun saveProfile() {
        viewModelScope.launch {
            _saveProfileUIStateLiveData.postValue(SaveProfileUIState.ofLoading())
            val response = saveProfileUseCase.invoke()
            val saveProfileUIState = if (response.isSuccess()) {
                SaveProfileUIState.ofSuccess()
            } else {
                SaveProfileUIState.ofError(response.exception)
            }
            _saveProfileUIStateLiveData.postValue(saveProfileUIState)
        }
    }
}