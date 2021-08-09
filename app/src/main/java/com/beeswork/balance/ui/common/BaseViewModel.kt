package com.beeswork.balance.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.internal.exception.*
import kotlinx.coroutines.*

abstract class BaseViewModel : ViewModel() {

    private val _exceptionLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = _exceptionLiveData

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is AccountNotFoundException,
            is AccountDeletedException,
            is AccountBlockedException,
            is RefreshTokenExpiredException -> _exceptionLiveData.postValue(throwable)
            else -> throw throwable
        }
    }

    fun <T> viewModelLazyDeferred(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>> {
        return lazy {
            viewModelScope.async(start = CoroutineStart.LAZY) { block.invoke(this) }
        }
    }
}

//    protected fun <T> validateAccount(resource: Resource<T>): Boolean {
//        return if (resource.isError()) resource.error?.let { error ->
//            return when (error) {
//                ExceptionCode.ACCOUNT_BLOCKED_EXCEPTION,
//                ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,
//                ExceptionCode.ACCOUNT_DELETED_EXCEPTION -> {
//                    _exceptionLiveData.postValue(resource.toEmptyResponse())
//                    false
//                }
//                else -> true
//            }
//        } ?: true
//        else true
//    }