package com.beeswork.balance.ui.loginactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.domain.uistate.login.LoginUIState
import com.beeswork.balance.domain.usecase.login.SocialLoginUseCase
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.exception.InvalidSocialLoginException
import com.beeswork.balance.internal.mapper.login.LoginMapper
import kotlinx.coroutines.launch

class LoginViewModel(
    private val socialLoginUseCase: SocialLoginUseCase
) : ViewModel() {

    private val _loginLiveData = MutableLiveData<LoginUIState>()
    val loginLiveData: LiveData<LoginUIState> get() = _loginLiveData

    fun socialLogin(loginId: String?, accessToken: String?, loginType: LoginType) {
        if (loginId.isNullOrBlank() || accessToken.isNullOrBlank()) {
            _loginLiveData.postValue(LoginUIState.ofError(InvalidSocialLoginException()))
        } else {
            viewModelScope.launch {
                val response = socialLoginUseCase.invoke(loginId, accessToken, loginType)
                val loginUIState = if (response.isSuccess() && response.data != null) {
                    LoginUIState.ofSuccess(response.data.profileExists)
                } else {
                    LoginUIState.ofError(response.exception)
                }
                _loginLiveData.postValue(loginUIState)
            }
        }
    }
}