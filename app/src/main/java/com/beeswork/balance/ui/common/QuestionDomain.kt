package com.beeswork.balance.ui.common

data class QuestionDomain(
    val id: Int,
    val description: String,
    val topOption: String,
    val bottomOption:  String,
    val answer: Boolean?
)