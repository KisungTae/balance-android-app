package com.beeswork.balance.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val profileMapper: ProfileMapper
) : ViewModel() {

    private val _profileLiveData = MutableLiveData<ProfileDomain>()
    val profileLiveData: LiveData<ProfileDomain> get() = _profileLiveData


    fun fetchProfile() {
        viewModelScope.launch {
            _profileLiveData.postValue(profileMapper.toProfileDomain(profileRepository.fetchProfile()))
        }
    }

    fun test() {
        profileRepository.test()
    }
}