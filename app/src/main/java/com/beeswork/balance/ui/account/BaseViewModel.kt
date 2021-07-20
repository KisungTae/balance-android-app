package com.beeswork.balance.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.IdentityTokenNotFoundException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private val _invalidAccountExceptionLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val invalidAccountExceptionLiveData: LiveData<Resource<EmptyResponse>> get() = _invalidAccountExceptionLiveData

    protected fun viewModelScopeSafeLaunch(body: suspend () -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            when (throwable) {
                is AccountIdNotFoundException, is IdentityTokenNotFoundException -> {
                    println("is AccountIdNotFoundException, is IdentityTokenNotFoundException ->")
                    _invalidAccountExceptionLiveData.postValue(Resource.error(ExceptionCode.ACCOUNT_IDENTITY_NOT_FOUND_EXCEPTION))
                }

                else -> throw throwable
            }
        }) { body.invoke() }
    }

    protected fun <T> validateAccount(resource: Resource<T>) {
        if (resource.isError()) resource.error?.let { error ->
            when (error) {
                ExceptionCode.ACCOUNT_BLOCKED_EXCEPTION,
                ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,
                ExceptionCode.ACCOUNT_DELETED_EXCEPTION -> {
                    println("ExceptionCode.ACCOUNT_BLOCKED_EXCEPTION,\n" +
                            "                ExceptionCode.ACCOUNT_NOT_FOUND_EXCEPTION,\n" +
                            "                ExceptionCode.ACCOUNT_DELETED_EXCEPTION")
                    _invalidAccountExceptionLiveData.postValue(resource.toEmptyResponse())
                }
                else -> println()
            }
        }
    }

}