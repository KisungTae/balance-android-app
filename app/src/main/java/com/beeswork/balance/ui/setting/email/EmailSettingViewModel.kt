package com.beeswork.balance.ui.setting.email

import android.view.View
import androidx.lifecycle.*
import com.beeswork.balance.R
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.AccountNotFoundException
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class EmailSettingViewModel(
    private val settingRepository: SettingRepository
) : BaseViewModel() {

    val emailLiveData by viewModelLazyDeferred { settingRepository.getEmailFlow().asLiveData() }

    private val _saveEmailLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveEmailLiveData: LiveData<Resource<EmptyResponse>> get() = _saveEmailLiveData

    private val _fetchEmailLiveData = MutableLiveData<Resource<String>>()
    val fetchEmailLiveData: LiveData<Resource<String>> get() = _fetchEmailLiveData

    fun saveEmail(email: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _saveEmailLiveData.postValue(Resource.loading())
            val emailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val response = if (emailValid) settingRepository.saveEmail(email)
            else Resource.error(ExceptionCode.INVALID_EMAIL_EXCEPTION)
            _saveEmailLiveData.postValue(response)
        }
    }

    fun fetchEmail() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _fetchEmailLiveData.postValue(Resource.loading())
            _fetchEmailLiveData.postValue(settingRepository.fetchEmail())
        }
    }
}