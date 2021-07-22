package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

class AccountIdNotFoundException(
    val error: String = "account_id_not_found_exception"
): RuntimeException()