package com.beeswork.balance.ui.registeractivity.birthdate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.uistate.register.RegisterStepUIState
import com.beeswork.balance.domain.usecase.register.GetBirthDateUseCase
import com.beeswork.balance.domain.usecase.register.SaveBirthDateUseCase
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

class BirthDateStepViewModel(
    private val getBirthDateUseCase: GetBirthDateUseCase,
    private val saveBirthDateUseCase: SaveBirthDateUseCase
) : ViewModel() {

    private val _birthDateLiveData = MutableLiveData<LocalDate?>()
    val birthDateLiveData: LiveData<LocalDate?> get() = _birthDateLiveData

    private val _saveBirthDateUIStateLiveData = MutableLiveData<RegisterStepUIState>()
    val saveBirthDateUIStateLiveData: LiveData<RegisterStepUIState> get() = _saveBirthDateUIStateLiveData

    fun getBirthDate() {
        viewModelScope.launch {
            _birthDateLiveData.postValue(getBirthDateUseCase.invoke())
        }
    }

    fun saveBirthDate(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            val response = saveBirthDateUseCase.invoke(year, month, day)
            val saveBirthDateUIState = if (response.isSuccess()) {
                RegisterStepUIState.ofSuccess()
            } else {
                RegisterStepUIState.ofError(response.exception)
            }
            _saveBirthDateUIStateLiveData.postValue(saveBirthDateUIState)
        }
    }
}