package com.beeswork.balance.ui.setting.email

import android.view.View
import androidx.lifecycle.*
import com.beeswork.balance.R
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.exception.AccountNotFoundException
import com.beeswork.balance.internal.mapper.setting.LoginMapper
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EmailSettingViewModel(
    private val loginRepository: LoginRepository,
    private val loginMapper: LoginMapper
) : BaseViewModel() {

    private val _saveEmailLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveEmailLiveData: LiveData<Resource<EmptyResponse>> get() = _saveEmailLiveData

    private val _fetchEmailLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val fetchEmailLiveData: LiveData<Resource<EmptyResponse>> get() = _fetchEmailLiveData

    private val _emailLiveData = MutableLiveData<Resource<String>>()
    val emailLiveData: LiveData<Resource<String>> get() = _emailLiveData

    private val _loginTypeLiveData = MutableLiveData<LoginType>()
    val loginTypeLiveData: LiveData<LoginType> get() = _loginTypeLiveData

    fun fetchLoginType() {
        viewModelScope.launch { _loginTypeLiveData.postValue(loginRepository.getLoginType()) }
    }

    fun saveEmail(email: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _saveEmailLiveData.postValue(Resource.loading())
            val isEmailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            if (isEmailValid) {
                val response = loginRepository.saveEmail(email)
                _saveEmailLiveData.postValue(response)
            } else _saveEmailLiveData.postValue(Resource.error(ExceptionCode.INVALID_EMAIL_EXCEPTION))
        }
    }

    fun fetchEmail() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _emailLiveData.postValue(Resource.success(loginRepository.getEmail()))
            if (!loginRepository.isEmailSynced()) {
                _emailLiveData.postValue(Resource.loading())
                _emailLiveData.postValue(loginRepository.fetchEmail())
            }
        }
    }
}