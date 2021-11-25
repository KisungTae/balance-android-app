package com.beeswork.balance.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.internal.exception.*
import kotlinx.coroutines.*

abstract class BaseViewModel : ViewModel() {

    fun <T> viewModelLazyDeferred(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>> {
        return lazy {
            viewModelScope.async(start = CoroutineStart.LAZY) { block.invoke(this) }
        }
    }
}