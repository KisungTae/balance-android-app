package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

class AccountIdNotFoundException(
    errorMessage: String?
): RuntimeException(errorMessage)  {

    constructor() : this(null)
}