package com.beeswork.balance.data.network.response

data class ExceptionResponse(
    val status: Int,
    val error: String,
    val message: String,
    val fieldErrorMessages: Map<String, String>
)