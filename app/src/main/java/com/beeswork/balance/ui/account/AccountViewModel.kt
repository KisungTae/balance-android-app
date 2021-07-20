package com.beeswork.balance.ui.account

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.internal.util.lazyDeferred

class AccountViewModel(
    private val settingRepository: SettingRepository,
    private val photoRepository: PhotoRepository,
    private val profileRepository: ProfileRepository
): BaseViewModel() {

    val emailLiveData by lazyDeferred {
        settingRepository.getEmailFlow().asLiveData()
    }

    val profilePhotoKeyLiveData by lazyDeferred {
        photoRepository.getProfilePhotoKeyFlow().asLiveData()
    }

    val nameLiveData by lazyDeferred {
        profileRepository.getNameFlow().asLiveData()
    }

    private val _fetchQuestionsLiveData = MutableLiveData<Resource<List<QuestionDTO>>>()
    val fetchQuestionsLiveData: LiveData<Resource<List<QuestionDTO>>> get() = _fetchQuestionsLiveData

    fun fetchQuestions() {
        viewModelScopeSafeLaunch {
            _fetchQuestionsLiveData.postValue(Resource.loading())
            val response = profileRepository.fetchQuestions()
            validateAccount(response)
            _fetchQuestionsLiveData.postValue(response)
        }
    }

}