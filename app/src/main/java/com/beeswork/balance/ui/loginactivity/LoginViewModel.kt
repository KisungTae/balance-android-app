package com.beeswork.balance.ui.loginactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.mapper.login.LoginMapper
import com.beeswork.balance.internal.util.safeLet
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val settingRepository: SettingRepository,
    private val swipeRepository: SwipeRepository,
    private val loginMapper: LoginMapper
) : ViewModel() {

    private val _loginLiveData = MutableLiveData<Resource<LoginDomain>>()
    val loginLiveData: LiveData<Resource<LoginDomain>> get() = _loginLiveData

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _loginLiveData.postValue(Resource.error(null, throwable.message))
    }

    fun socialLogin(loginId: String?, accessToken: String?, loginType: LoginType) {
        safeLet(loginId, accessToken) { _loginId, _accessToken ->
            viewModelScope.launch(coroutineExceptionHandler) {
                val response = loginRepository.socialLogin(_loginId, _accessToken, loginType)
                if (response.isSuccess()) response.data?.let { loginDTO ->
                    settingRepository.prepopulateFetchInfo()
                    if (loginDTO.profileExists) swipeRepository.prepopulateSwipeFilter(loginDTO.gender)
                    settingRepository.syncFCMTokenAsync()
                }
                _loginLiveData.postValue(
                    response.let { it.mapData(it.data?.let { loginDTO -> loginMapper.toLoginDomain(loginDTO) }) }
                )
            }
        } ?: kotlin.run {
            _loginLiveData.postValue(Resource.error(ExceptionCode.INVALID_SOCIAL_LOGIN_EXCEPTION))
        }
    }
}