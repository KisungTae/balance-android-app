package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

class AccessTokenNotFoundException(
    errorMessage: String?
): RuntimeException(errorMessage)   {

    constructor() : this(null)
}