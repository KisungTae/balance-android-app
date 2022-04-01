package com.beeswork.balance.domain.usecase.main

interface SaveLocationPermissionUseCase {

    suspend fun invoke(granted: Boolean)
}