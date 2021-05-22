package com.beeswork.balance.data.network.response.profile

data class QuestionDTO(
    val id: Int,
    val description: String,
    val topOption: String,
    val bottomOption:  String,
    val answer: Boolean?
)