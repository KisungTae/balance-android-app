package com.beeswork.balance.domain.usecase.register

interface GetNameUseCase {

    suspend fun invoke(): String?
}