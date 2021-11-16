package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

class IdentityTokenNotFoundException(
    errorMessage: String?
) : RuntimeException(errorMessage) {
    constructor() : this(null)
}