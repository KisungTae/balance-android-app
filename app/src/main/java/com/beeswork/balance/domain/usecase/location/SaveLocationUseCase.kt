package com.beeswork.balance.domain.usecase.location

interface SaveLocationUseCase {

    suspend fun invoke(latitude: Double, longitude: Double, syncLocation: Boolean)
}