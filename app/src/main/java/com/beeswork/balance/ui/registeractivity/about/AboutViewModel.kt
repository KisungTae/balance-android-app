package com.beeswork.balance.ui.registeractivity.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.domain.uistate.register.SaveProfileUIState
import com.beeswork.balance.domain.usecase.register.GetAboutUseCase
import com.beeswork.balance.domain.usecase.register.SaveAboutUseCase
import kotlinx.coroutines.launch

class AboutViewModel (
    private val getAboutUseCase: GetAboutUseCase,
    private val saveAboutUseCase: SaveAboutUseCase
): ViewModel() {

    private val _aboutLiveData = MutableLiveData<String?>()
    val aboutLiveData: LiveData<String?> get() = _aboutLiveData

    private val _saveAboutUIStateLiveData = MutableLiveData<SaveProfileUIState>()
    val saveAboutUIStateLiveData: LiveData<SaveProfileUIState> get() = _saveAboutUIStateLiveData

    fun getAbout() {
        viewModelScope.launch {
            _aboutLiveData.postValue(getAboutUseCase.invoke())
        }
    }

    fun saveAbout(about: String) {
        viewModelScope.launch {
            val response = saveAboutUseCase.invoke(about)
            val saveAboutUIState = if (response.isSuccess()) {
                SaveProfileUIState.ofSuccess()
            } else {
                SaveProfileUIState.ofError(response.exception)
            }
            _saveAboutUIStateLiveData.postValue(saveAboutUIState)
        }
    }
}