package com.beeswork.balance.internal.exception

class ServerException(
    code: String?,
    message: String?,
    val fieldErrors: Map<String, String>?
) : BaseException(code, message) {

    constructor(code: String?, message: String?) : this(code, message, null)
}