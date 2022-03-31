package com.beeswork.balance.ui.registeractivity.name

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.uistate.register.SaveProfileUIState
import com.beeswork.balance.domain.usecase.register.GetNameUseCase
import com.beeswork.balance.domain.usecase.register.SaveNameUseCase
import kotlinx.coroutines.launch

class NameViewModel(
    private val getNameUseCase: GetNameUseCase,
    private val saveNameUseCase: SaveNameUseCase
) : ViewModel() {

    private val _nameLiveData = MutableLiveData<String?>()
    val nameLiveData: LiveData<String?> get() = _nameLiveData

    private val _saveNameUIStateLiveData = MutableLiveData<SaveProfileUIState>()
    val saveNameUIStateLiveData: LiveData<SaveProfileUIState> get() = _saveNameUIStateLiveData

    fun getName() {
        viewModelScope.launch {
            _nameLiveData.postValue(getNameUseCase.invoke())
        }
    }

    fun saveName(name: String) {
        viewModelScope.launch {
            val response = saveNameUseCase.invoke(name)
            val saveNameUIState = if (response.isSuccess()) {
                SaveProfileUIState.ofSuccess()
            } else {
                SaveProfileUIState.ofError(response.exception)
            }
            _saveNameUIStateLiveData.postValue(saveNameUIState)
        }
    }
}