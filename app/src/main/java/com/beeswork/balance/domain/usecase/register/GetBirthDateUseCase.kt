package com.beeswork.balance.domain.usecase.register

import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

interface GetBirthDateUseCase {

    suspend fun invoke(): LocalDate?
}