package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

class InvalidRefreshTokenException(
    val errorMessage: String?
): RuntimeException(errorMessage)