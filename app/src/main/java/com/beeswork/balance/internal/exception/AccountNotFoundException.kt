package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

class AccountNotFoundException(
    errorMessage: String?
): RuntimeException(errorMessage)