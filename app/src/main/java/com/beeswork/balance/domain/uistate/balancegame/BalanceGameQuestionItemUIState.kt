package com.beeswork.balance.domain.uistate.balancegame

data class BalanceGameQuestionItemUIState(
    val id: Int,
    val description: String,
    val topOption: String,
    val bottomOption:  String,
    var answer: Boolean?
)