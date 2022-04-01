package com.beeswork.balance.domain.usecase.main

interface SaveLocationUseCase {

    suspend fun invoke(latitude: Double, longitude: Double)
}