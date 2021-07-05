package com.beeswork.balance.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.util.safeLet
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel(
    private val loginRepository: LoginRepository
): ViewModel() {

    private val _loginLiveData = MutableLiveData<Resource<LoginDTO>>()
    val loginLiveData: LiveData<Resource<LoginDTO>> get() = _loginLiveData

    fun login() {

    }

    fun socialLogin(loginId: String?, accessToken: String?, loginType: LoginType) {
        safeLet(loginId, accessToken) { _loginId, _accessToken ->
            viewModelScope.launch {
                _loginLiveData.postValue(loginRepository.socialLogin(_loginId, _accessToken, loginType))
            }
        } ?: kotlin.run {
            _loginLiveData.postValue(Resource.error(ExceptionCode.INVALID_SOCIAL_LOGIN_EXCEPTION))
        }
    }
}