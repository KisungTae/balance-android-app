package com.beeswork.balance.data.network.response

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val fieldErrorMessages: Map<String, String>
)