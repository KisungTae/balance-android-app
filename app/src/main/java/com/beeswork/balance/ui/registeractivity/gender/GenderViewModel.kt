package com.beeswork.balance.ui.registeractivity.gender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.uistate.register.SaveProfileUIState
import com.beeswork.balance.domain.usecase.register.GetGenderUseCase
import com.beeswork.balance.domain.usecase.register.SaveGenderUseCase
import kotlinx.coroutines.launch

class GenderViewModel (
    private val getGenderUseCase: GetGenderUseCase,
    private val saveGenderUseCase: SaveGenderUseCase
): ViewModel() {

    private val _genderLiveData = MutableLiveData<Boolean?>()
    val genderLiveData: LiveData<Boolean?> get() = _genderLiveData

    private val _saveGenderUIStateLiveData = MutableLiveData<SaveProfileUIState>()
    val saveGenderUIStateLiveData: LiveData<SaveProfileUIState> get() = _saveGenderUIStateLiveData

    fun getGender() {
        viewModelScope.launch {
            _genderLiveData.postValue(getGenderUseCase.invoke())
        }
    }

    fun saveGender(gender: Boolean) {
        viewModelScope.launch {
            val response = saveGenderUseCase.invoke(gender)
            val saveGenderUIState = if (response.isSuccess()) {
                SaveProfileUIState.ofSuccess()
            } else {
                SaveProfileUIState.ofError(response.exception)
            }
            _saveGenderUIStateLiveData.postValue(saveGenderUIState)
        }
    }
}