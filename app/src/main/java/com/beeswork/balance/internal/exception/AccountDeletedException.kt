package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

class AccountDeletedException(
    errorMessage: String?
) : RuntimeException(errorMessage)