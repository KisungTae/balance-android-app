package com.beeswork.balance.domain.usecase.location

import com.beeswork.balance.internal.constant.LocationPermissionStatus

interface UpdateLocationGrantedUseCase {

    suspend fun invoke(locationPermissionStatus: LocationPermissionStatus)
}