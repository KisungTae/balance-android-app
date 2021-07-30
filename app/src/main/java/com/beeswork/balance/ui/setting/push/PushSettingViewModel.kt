package com.beeswork.balance.ui.setting.push

import androidx.lifecycle.*
import com.beeswork.balance.data.database.entity.PushSetting
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.mapper.setting.PushSettingMapper
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class PushSettingViewModel(
    private val settingRepository: SettingRepository,
    private val pushSettingMapper: PushSettingMapper
) : BaseViewModel() {

    private val _fetchPushSettingLiveData = MutableLiveData<Resource<PushSettingDomain>>()
    val fetchPushSettingLiveData: LiveData<Resource<PushSettingDomain>> get() = _fetchPushSettingLiveData

    fun fetchPushSetting() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val pushSetting = settingRepository.getPushSetting()
            pushSetting?.let { _pushSetting -> postSuccessFetchPushSettingLiveData(_pushSetting) }
            val isPushSettingSynced = pushSetting?.synced ?: false
            if (!isPushSettingSynced) {
                _fetchPushSettingLiveData.postValue(Resource.loading())
                val response = settingRepository.fetchPushSetting()
                if (response.isSuccess()) response.data?.let { _pushSetting ->
                    postSuccessFetchPushSettingLiveData(_pushSetting)
                } else _fetchPushSettingLiveData.postValue(response.mapData(null))
            }
        }
    }

    private fun postSuccessFetchPushSettingLiveData(pushSetting: PushSetting) {
        val pushSettingDomain = pushSettingMapper.toPushSettingDomain(pushSetting)
        _fetchPushSettingLiveData.postValue(Resource.success(pushSettingDomain))
    }
}