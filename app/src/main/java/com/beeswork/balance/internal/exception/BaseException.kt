package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

open class BaseException(
    val error: String?,
    message: String?
): RuntimeException(message)