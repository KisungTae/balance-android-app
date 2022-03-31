package com.beeswork.balance.domain.usecase.register

interface GetAboutUseCase {

    suspend fun invoke(): String?
}