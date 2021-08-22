package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

class ExpiredJWTException(
    val errorMessage: String?
): RuntimeException(errorMessage)