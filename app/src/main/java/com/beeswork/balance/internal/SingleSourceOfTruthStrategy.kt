package com.beeswork.balance.internal

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers

fun <T, A> resourceLiveData (
    databaseQuery: () -> LiveData<T>,
    networkCall: suspend () -> Resource<A>,
    saveCallResult: suspend (A) -> Unit
): LiveData<Resource<T>> = liveData(Dispatchers.IO) {

    emit(Resource.loading<T>())

    val source = databaseQuery.invoke().map { Resource.success(it) }
    emitSource(source)

    val responseStatus = networkCall.invoke()
    if (responseStatus.status == Resource.Status.SUCCESS) {
        saveCallResult(responseStatus.data!!)
    } else if (responseStatus.status == Resource.Status.EXCEPTION) {
        emit(Resource.exception<T>(responseStatus.exceptionMessage!!))
        emitSource(source)
    }
}