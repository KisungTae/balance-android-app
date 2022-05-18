package com.beeswork.balance.domain.usecase.login

interface GetEmailUseCase {

    suspend fun invoke(): String?
}