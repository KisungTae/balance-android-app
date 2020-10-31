package com.beeswork.balance.data.network.response

data class QuestionResponse(
    val id: Long,
    val description: String,
    val topOption: String,
    val bottomOption: String
)
