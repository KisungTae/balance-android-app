package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

class AccountBlockedException(
    errorMessage: String?
): RuntimeException(errorMessage)