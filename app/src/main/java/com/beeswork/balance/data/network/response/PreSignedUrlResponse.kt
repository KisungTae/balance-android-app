package com.beeswork.balance.data.network.response

data class PreSignedUrlResponse(
    val url: String,
    val fields: Map<String, String>
)