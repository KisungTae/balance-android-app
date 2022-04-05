package com.beeswork.balance.internal.mapper.profile

import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.domain.uistate.balancegame.QuestionItemUIState
import com.beeswork.balance.ui.common.QuestionDomain

interface QuestionMapper {
    fun toQuestionDomain(questionDTO: QuestionDTO): QuestionDomain
    fun toQuestionItemUIState(questionDTO: QuestionDTO): QuestionItemUIState
}