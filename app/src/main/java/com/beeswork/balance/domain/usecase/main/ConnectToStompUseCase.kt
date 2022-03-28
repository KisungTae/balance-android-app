package com.beeswork.balance.domain.usecase.main

interface ConnectToStompUseCase {
    suspend fun invoke(forceToConnect: Boolean)
}