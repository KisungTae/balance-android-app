package com.beeswork.balance.data.network.response

data class Question(
    val description: String,
    val topOption: String,
    val bottomOption: String,
    val selected: Boolean
)
