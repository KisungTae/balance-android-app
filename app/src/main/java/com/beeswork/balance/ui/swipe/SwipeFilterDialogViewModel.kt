package com.beeswork.balance.ui.swipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.internal.mapper.swipe.SwipeFilterMapper
import kotlinx.coroutines.launch

class SwipeFilterDialogViewModel(
    private val swipeRepository: SwipeRepository,
    private val swipeFilterMapper: SwipeFilterMapper
): ViewModel() {

    private val _swipeFilterLiveData = MutableLiveData<SwipeFilterDomain>()
    val swipeFilterLiveData: LiveData<SwipeFilterDomain> get() = _swipeFilterLiveData

    private val _saveSwipeFilterLiveData = MutableLiveData<Any?>()
    val saveSwipeFilterLiveData: LiveData<Any?> get() = _saveSwipeFilterLiveData

    fun fetchSwipeFilter() {
        viewModelScope.launch {
            val swipeFilter = swipeRepository.getSwipeFilter()
            _swipeFilterLiveData.postValue(swipeFilterMapper.toSwipeFilterDomain(swipeFilter))
        }
    }

    fun saveSwipeFilter(gender: Gender, minAge: Int, maxAge: Int, distance: Int) {
        viewModelScope.launch {
            swipeRepository.saveSwipeFilter(gender, minAge, maxAge, distance)
        }
    }
}