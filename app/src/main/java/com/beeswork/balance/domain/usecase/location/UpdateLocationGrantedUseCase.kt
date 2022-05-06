package com.beeswork.balance.domain.usecase.location

interface UpdateLocationGrantedUseCase {

    suspend fun invoke(granted: Boolean)
}