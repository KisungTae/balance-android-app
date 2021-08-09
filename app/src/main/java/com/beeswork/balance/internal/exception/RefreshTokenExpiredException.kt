package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

class RefreshTokenExpiredException(
    val errorMessage: String?
): RuntimeException(errorMessage)