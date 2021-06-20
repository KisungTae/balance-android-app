package com.beeswork.balance.ui.setting.email

import android.view.View
import androidx.lifecycle.*
import com.beeswork.balance.R
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.util.lazyDeferred
import kotlinx.coroutines.launch

class EmailSettingViewModel(
    private val settingRepository: SettingRepository
) : ViewModel() {

    val emailLiveData by lazyDeferred { settingRepository.getEmailFlow().asLiveData() }

    private val _saveEmailLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveEmailLiveData: LiveData<Resource<EmptyResponse>> get() = _saveEmailLiveData

    fun saveEmail(email: String) {
        viewModelScope.launch {
            _saveEmailLiveData.postValue(Resource.loading())
            val emailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val response = if (emailValid) settingRepository.saveEmail(email)
            else Resource.error(ExceptionCode.INVALID_EMAIL_EXCEPTION)
            _saveEmailLiveData.postValue(response)
        }
    }

    fun fetchEmail() {
        viewModelScope.launch { settingRepository.fetchEmail() }
    }
}