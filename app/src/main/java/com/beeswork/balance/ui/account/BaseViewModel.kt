package com.beeswork.balance.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.*
import kotlinx.coroutines.*
import java.lang.RuntimeException

abstract class BaseViewModel : ViewModel() {
    private val _exceptionLiveData = MutableLiveData<Throwable>()
    val exceptionLiveData: LiveData<Throwable> get() = _exceptionLiveData



    protected fun viewModelScopeSafeLaunch(body: suspend () -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            when (throwable) {
                is AccountIdNotFoundException,
                is IdentityTokenNotFoundException,
                is AccountNotFoundException,
                is AccountDeletedException,
                is AccountBlockedException -> {
                    println("is AccountIdNotFoundException, is IdentityTokenNotFoundException ->")
                    _exceptionLiveData.postValue(throwable)
                }
                else -> throw throwable
            }
        }) { body.invoke() }
    }

    fun <T> safeLazyDeferred(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>> {
        return lazy {
            viewModelScope.async(CoroutineExceptionHandler { _, throwable -> }, start = CoroutineStart.LAZY) {

            }


            viewModelScope.async(start = CoroutineStart.LAZY,) {

            }
            GlobalScope.async(start = CoroutineStart.LAZY) {
                block.invoke(this)
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

}