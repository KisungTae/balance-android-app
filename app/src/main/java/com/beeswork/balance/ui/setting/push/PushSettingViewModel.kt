package com.beeswork.balance.ui.setting.push

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PushSettingViewModel(
    private val settingRepository: SettingRepository
) : BaseViewModel() {

    val pushSettings by viewModelLazyDeferred { settingRepository.getPushSettingsFlow().asLiveData() }

    private val _saveMatchPushLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveMatchPushLiveData: LiveData<Resource<EmptyResponse>> get() = _saveMatchPushLiveData

    private val _saveClickedPushLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveClickedPushLiveData: LiveData<Resource<EmptyResponse>> get() = _saveClickedPushLiveData

    private val _saveChatMessagePushLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveChatMessagePushLiveData: LiveData<Resource<EmptyResponse>> get() = _saveChatMessagePushLiveData

    fun syncPushSettings() {
        viewModelScope.launch(coroutineExceptionHandler) {
//            val setting = settingRepository.getSetting()
//            if (setting.matchPushSynced && setting.clickedPushSynced && setting.chatMessagePushSynced) return@launch
//
//            val matchPush = if (setting.matchPushSynced) null else setting.matchPush
//            val clickedPush = if (setting.clickedPushSynced) null else setting.clickedPush
//            val chatMessagePush = if (setting.clickedPushSynced) null else setting.chatMessagePush
//
//            matchPush?.let { _saveMatchPushLiveData.postValue(Resource.loading()) }
//            clickedPush?.let { _saveClickedPushLiveData.postValue(Resource.loading()) }
//            chatMessagePush?.let { _saveChatMessagePushLiveData.postValue(Resource.loading()) }
//
//            val response = settingRepository.syncPushSettings(matchPush, clickedPush, chatMessagePush)
//
//            matchPush?.let { _saveMatchPushLiveData.postValue(response) }
//            clickedPush?.let { _saveClickedPushLiveData.postValue(response) }
//            chatMessagePush?.let { _saveChatMessagePushLiveData.postValue(response) }
        }
    }

    fun saveMatchPush(matchPush: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (settingRepository.getMatchPush() != matchPush) {
                _saveMatchPushLiveData.postValue(Resource.loading())
                _saveMatchPushLiveData.postValue(settingRepository.saveMatchPush(matchPush))
            }
        }
    }

    fun saveClickedPush(clickedPush: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (settingRepository.getClickedPush() != clickedPush) {
                _saveClickedPushLiveData.postValue(Resource.loading())
                _saveClickedPushLiveData.postValue(settingRepository.saveClickedPush(clickedPush))
            }
        }
    }

    fun saveChatMessagePush(chatMessagePush: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (settingRepository.getChatMessagePush() != chatMessagePush) {
                _saveChatMessagePushLiveData.postValue(Resource.loading())
                _saveChatMessagePushLiveData.postValue(settingRepository.saveChatMessagePush(chatMessagePush))
            }
        }
    }

    fun test() {
//        _syncPushSettingsLiveData.postValue(Resource.loading())
//        viewModelScope.launch {
//            delay(2000)
//            _saveMatchPushLiveData.postValue(Resource.error("err"))
//            delay(2000)
//            _saveClickedPushLiveData.postValue(Resource.error("err"))
//            delay(2000)
//            _saveChatMessagePushLiveData.postValue(Resource.error("err"))
//        }
    }

}