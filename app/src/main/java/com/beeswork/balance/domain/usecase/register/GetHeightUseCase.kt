package com.beeswork.balance.domain.usecase.register

interface GetHeightUseCase {

    suspend fun invoke(): Int?
}