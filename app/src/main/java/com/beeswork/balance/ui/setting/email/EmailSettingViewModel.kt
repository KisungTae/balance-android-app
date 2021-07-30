package com.beeswork.balance.ui.setting.email

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class EmailSettingViewModel(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    private val _fetchEmailLiveData = MutableLiveData<Resource<String>>()
    val fetchEmailLiveData: LiveData<Resource<String>> get() = _fetchEmailLiveData

    private val _loginTypeLiveData = MutableLiveData<LoginType>()
    val loginTypeLiveData: LiveData<LoginType> get() = _loginTypeLiveData

    private val _saveEmailLiveData = MutableLiveData<Resource<String>>()
    val saveEmailLiveData: LiveData<Resource<String>> get() = _saveEmailLiveData


    fun fetchLoginType() {
        viewModelScope.launch { _loginTypeLiveData.postValue(loginRepository.getLoginType()) }
    }

    fun saveEmail(email: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _saveEmailLiveData.postValue(Resource.loading())
            if (isEmailValid(email)) {
                val response = loginRepository.saveEmail(email)
                _saveEmailLiveData.postValue(response)
            } else {
                val response = Resource.errorWithData(loginRepository.getEmail(), ExceptionCode.INVALID_EMAIL_EXCEPTION)
                _saveEmailLiveData.postValue(response)
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun fetchEmail() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _fetchEmailLiveData.postValue(Resource.success(loginRepository.getEmail()))
            if (!loginRepository.isEmailSynced()) {
                _fetchEmailLiveData.postValue(Resource.loading())
                _fetchEmailLiveData.postValue(loginRepository.fetchEmail())
            }
        }
    }
}