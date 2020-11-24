package com.beeswork.balance.data.network.response

data class QuestionResponse(
    val id: Int,
    val description: String,
    val topOption: String,
    val bottomOption: String,
    val answer: Boolean?
)
