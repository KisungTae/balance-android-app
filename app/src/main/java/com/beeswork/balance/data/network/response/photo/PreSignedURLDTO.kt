package com.beeswork.balance.data.network.response.photo

data class PreSignedURLDTO(
    val url: String,
    val fields: Map<String, String>
)