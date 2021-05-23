package com.beeswork.balance.ui.profile

data class QuestionDomain(
    val id: Int,
    val description: String,
    val topOption: String,
    val bottomOption:  String,
    val answer: Boolean?
)