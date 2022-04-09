package com.beeswork.balance.domain.usecase.photo

interface GetProfilePhotoUrlUseCase {

    suspend fun invoke(): String?
}