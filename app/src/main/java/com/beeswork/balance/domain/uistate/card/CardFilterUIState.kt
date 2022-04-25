package com.beeswork.balance.domain.uistate.card

import com.beeswork.balance.internal.constant.CardFilterConstant

data class CardFilterUIState(
    val gender: Boolean? = null,
    val minAge: Int = CardFilterConstant.MIN_AGE,
    val maxAge: Int = CardFilterConstant.MAX_AGE,
    val distance: Int = CardFilterConstant.MAX_DISTANCE
)