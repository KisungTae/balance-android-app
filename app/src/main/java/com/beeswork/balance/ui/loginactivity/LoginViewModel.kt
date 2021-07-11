package com.beeswork.balance.ui.loginactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.util.safeLet
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val settingRepository: SettingRepository,
    private val swipeRepository: SwipeRepository
): ViewModel() {

    private val _loginLiveData = MutableLiveData<Resource<LoginDTO>>()
    val loginLiveData: LiveData<Resource<LoginDTO>> get() = _loginLiveData

    fun login() {

    }

    fun socialLogin(loginId: String?, accessToken: String?, loginType: LoginType) {
        safeLet(loginId, accessToken) { _loginId, _accessToken ->
            viewModelScope.launch {
                val response = loginRepository.socialLogin(_loginId, _accessToken, loginType)
                if (response.isSuccess()) {
                    settingRepository.prepopulateFetchInfo()
                    swipeRepository.prepopulateSwipeFilter()
                }
                _loginLiveData.postValue(loginRepository.socialLogin(_loginId, _accessToken, loginType))
            }
        } ?: kotlin.run {
            _loginLiveData.postValue(Resource.error(ExceptionCode.INVALID_SOCIAL_LOGIN_EXCEPTION))
        }
    }
}