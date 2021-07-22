package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

class IdentityTokenNotFoundException(
    val error: String = "identity_token_not_found_exception"
): RuntimeException()