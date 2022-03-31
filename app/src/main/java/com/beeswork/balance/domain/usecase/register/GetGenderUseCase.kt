package com.beeswork.balance.domain.usecase.register

interface GetGenderUseCase {
    suspend fun invoke(): Boolean?
}